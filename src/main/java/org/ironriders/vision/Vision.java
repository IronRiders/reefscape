package org.ironriders.vision;

import static org.ironriders.elevator.ElevatorConstants.I;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

import javax.naming.NameNotFoundException;

import java.util.Optional;
import org.ironriders.lib.FieldUtils;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.proto.Photon;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import swervelib.SwerveDrive;

/**
 * Vision is not a subsystem. It has no commands because it does not need to.
 * This class is a utility class for the DriveSubsystem and controls all of the
 * apriltag processing and pose estimation.
 */
public class Vision {

    private List<VisionCamera> cams = new ArrayList<>();

    public Vision() {
        cams.add(new VisionCamera("front",
                createOffset(0, 0, 0, 0, 0),
                VecBuilder.fill(0, 0, 0)));
        cams.add(new VisionCamera("frontRight",
                createOffset(0, 0, 0, 0, 0),
                VecBuilder.fill(0, 0, 0)));
        cams.add(new VisionCamera("backLeft",
                createOffset(0, 0, 0, 0, 0),
                VecBuilder.fill(0, 0, 0)));
    }

    /**
     * Takes a swerve drive and adds pose estimate
     * 
     * @param swerveDrive The swerve drive.
     */
    public void addPoseEstimates(SwerveDrive swerveDrive) {

        for (VisionCamera v : cams) {
            Optional<EstimatedRobotPose> estimate = v.getEstimate();
            if (estimate.isPresent())
                swerveDrive.addVisionMeasurement(
                        estimate.get().estimatedPose.toPose2d(),
                        v.latestResult.getTimestampSeconds(),
                        v.deviations);
        }
    }

    public Transform3d createOffset(double x, double y, double z, double pitch, double yaw) {
        return new Transform3d(new Translation3d(x, y, z), new Rotation3d(0, pitch, yaw));
    }

    public VisionCamera getCamera(String name) throws NameNotFoundException {
        for (VisionCamera v : cams) {
            if (v.photonCamera.getName().equals(name))
                return v;
        }
        throw new NameNotFoundException("Camera with name '" + name + "' not found");
    }

    public class VisionCamera {

        private PhotonCamera photonCamera;
        private PhotonPoseEstimator estimator;
        private Matrix<N3, N1> deviations;

        private PhotonPipelineResult latestResult;
        private Optional<EstimatedRobotPose> currentEstimate;

        private VisionCamera(String camName, Transform3d offset, Matrix<N3, N1> deviations) {

            photonCamera = new PhotonCamera(camName);

            estimator = new PhotonPoseEstimator(
                    FieldUtils.FIELD_LAYOUT, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, offset);
            estimator.setMultiTagFallbackStrategy(PoseStrategy.AVERAGE_BEST_TARGETS);

            this.deviations = deviations;
        }

        /** Updates the camera/estimator, only run once per loop. */
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

        private Optional<EstimatedRobotPose> refineMacrodata(EstimatedRobotPose pose) {

            double minAmbiguity = 1;
            // find best ambiguity between all targets
            for (PhotonTrackedTarget t : pose.targetsUsed) {
                if (t.poseAmbiguity != -1 && t.poseAmbiguity < minAmbiguity)
                    minAmbiguity = t.poseAmbiguity;
            }
            if (minAmbiguity >= 0.3)
                return Optional.empty();

            return Optional.of(pose);
        }
    }
}
