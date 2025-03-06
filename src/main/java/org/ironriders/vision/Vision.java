package org.ironriders.vision;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

import java.util.Optional;

import org.ironriders.lib.field.FieldUtils;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import swervelib.SwerveDrive;

/**
 * Vision is not a subsystem. This class is a utility class for the
 * DriveSubsystem
 * and controls all of the apriltag processing and pose estimation.
 * 
 * (Why is it not a subsystem? Because it doesn't need to be.)
 */
public class Vision {

    private static final double AMBIGUITY_TOLERANCE = 0.4; // percentage
    private static final double DISTANCE_TOLERANCE = 2.5; // meters
    private SwerveDrive swerveDrive = null;
    private List<VisionCamera> cams = new ArrayList<>();

    public boolean hasPose=false;
    
    public Vision(SwerveDrive drive) {
        this.swerveDrive = drive;
        cams.add(new VisionCamera("frontLeft",
                createOffset(11.5, 11.5, 6.5, 15, 22.5),
                VecBuilder.fill(0.5, 0.5, 1.0)));
        cams.add(new VisionCamera("backLeft",
                createOffset(11.5, -11.5, 6.5, 15, 157.5),
                VecBuilder.fill(0.5, 0.5, 1.0)));
        // cams.add(new VisionCamera("backRight",
        //         createOffset(-11.5, 11.5, 6.5, 15, -157.5),
        //         VecBuilder.fill(0.5, 0.5, 1.0)));
        cams.add(new VisionCamera("frontRight",
                createOffset(11.5, -11.5, 6.5, 15, -22.5),
                VecBuilder.fill(0.5, 0.5, 1.0)));
    }

    public void addPoseEstimates() {

        for (VisionCamera v : cams) {
            Optional<EstimatedRobotPose> estimate = v.getEstimate();
            if (estimate.isPresent())
                swerveDrive.addVisionMeasurement(
                        estimate.get().estimatedPose.toPose2d(),
                        v.latestResult.getTimestampSeconds(),
                        v.deviations);
        }
    }

    /**
     * Utility method, creates an offset transform.
     * 
     * @param x     The x offset in inches.
     * @param y     The y offset in inches.
     * @param z     The z offset in inches.
     * @param pitch The pitch offset in degrees.
     * @param yaw   The yaw offset in degrees.
     */
    public Transform3d createOffset(double x, double y, double z, double pitch, double yaw) {
        return new Transform3d(
                new Translation3d(Units.inchesToMeters(x), Units.inchesToMeters(y), Units.inchesToMeters(z)),
                new Rotation3d(0, Units.degreesToRadians(pitch), Units.degreesToRadians(yaw)));
    }

    /**
     * Gets a camera based on supplied id, set in Photon Client GUI.
     * 
     * @throws RuntimeException If the camera name supplied does not exist.
     */
    public VisionCamera getCamera(String name) throws RuntimeException {
        for (VisionCamera v : cams) {
            if (v.photonCamera.getName().equals(name))
                return v;
        }
        throw new RuntimeException("Camera with name '" + name + "' not found");
    }

    public void updateAll() {
        for (VisionCamera v : cams) {
            v.update();
        }
    }

    /**
     * Class representing a single camera and its respective pose estimator and
     * standard deviations. Results and estimates are gettable.
     */
    public class VisionCamera {

        private PhotonCamera photonCamera;
        private PhotonPoseEstimator estimator;
        private Matrix<N3, N1> deviations;

        private PhotonPipelineResult latestResult;
        private Optional<EstimatedRobotPose> currentEstimate = Optional.empty();

        private VisionCamera(String camName, Transform3d offset, Matrix<N3, N1> deviations) {

            photonCamera = new PhotonCamera(camName);

            estimator = new PhotonPoseEstimator(
                    FieldUtils.FIELD_LAYOUT, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, offset);
            estimator.setMultiTagFallbackStrategy(PoseStrategy.AVERAGE_BEST_TARGETS);

            this.deviations = deviations;
        }

        /**
         * Updates the camera/estimator, only run once per loop.
         */
        public void update() {
            // check results, if none are good just return
            List<PhotonPipelineResult> results = photonCamera.getAllUnreadResults();
            if (results.isEmpty()) {
                currentEstimate = Optional.empty();
                return;
            }

            // find most recent result
            latestResult = results.get(0);
            for (PhotonPipelineResult r : results) {
                if (r.getTimestampSeconds() > latestResult.getTimestampSeconds())
                    latestResult = r;
            }

            Optional<EstimatedRobotPose> optional = estimator.update(latestResult);
            if (!optional.isPresent()) {
                currentEstimate = Optional.empty();
                return;
            }

            currentEstimate = refineMacrodata(optional.get());
        }

        /**
         * Gets the april tag fiducial id of the closest tag currently visible.
         * 
         * @return An optional int, empty if there are no targets visible.
         */
        public OptionalInt getClosestVisible() {
            if (!latestResult.hasTargets())
                return OptionalInt.empty();

            Iterator<PhotonTrackedTarget> iterator = latestResult.getTargets().iterator();
            PhotonTrackedTarget closest = iterator.next();

            while (iterator.hasNext()) {
                PhotonTrackedTarget target = iterator.next();
                if (target.getBestCameraToTarget().getX() < closest.getBestCameraToTarget().getX()) {
                    closest = target;
                }
            }

            return OptionalInt.of(closest.fiducialId);
        }

        public Optional<EstimatedRobotPose> getEstimate() {
            return currentEstimate;
        }

        /**
         * A TV show reference? In my code? It's more likely than you think.
         */
        private Optional<EstimatedRobotPose> refineMacrodata(EstimatedRobotPose pose) {

            double minAmbiguity = 1;
            // find best ambiguity between all targets
            for (PhotonTrackedTarget t : pose.targetsUsed) {
                if (t.poseAmbiguity != -1 && t.poseAmbiguity < minAmbiguity)
                    minAmbiguity = t.poseAmbiguity;
            }
            // trash past 30% ambiguity
            if (minAmbiguity >= AMBIGUITY_TOLERANCE)
                return Optional.empty();

            double minDistance = DISTANCE_TOLERANCE;
            // find closest distance between all targets
            for (PhotonTrackedTarget t : pose.targetsUsed) {
                double dist = Math
                        .sqrt(Math.pow(t.bestCameraToTarget.getX(), 2) + Math.pow(t.bestCameraToTarget.getY(), 2));

                if (dist < minDistance)
                    minDistance = dist;
            }
            // trash past tolerance
            if (minDistance >= DISTANCE_TOLERANCE)
                return Optional.empty();

            // trash if estimate is too far from the believed current pose
            // Transform2d differenceTransform = pose.estimatedPose.toPose2d().minus(swerveDrive.getPose());
            // if (Math.abs(differenceTransform.getX()) > 2 || Math.abs(differenceTransform.getY()) > 2) {
            //     return Optional.empty();
            // }

            // return actual estimate if it gets through all that ^^^
            hasPose=true;
            return Optional.of(pose);
        }
    }
}
