package org.ironriders.vision;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

import org.ironriders.core.FieldConstants;
import org.ironriders.drive.DriveSubsystem;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import swervelib.SwerveDrive;

public class Vision {

    List<PhotonCamera> cams = new ArrayList<>();
    List<PhotonPoseEstimator> poseEstimators = new ArrayList<>();

    public Vision() {

        for (VisionConstants.Camera cam : VisionConstants.CAMERAS) {
            cams.add(new PhotonCamera(cam.name));
            poseEstimators
                    .add(new PhotonPoseEstimator(FieldConstants.FIELD_LAYOUT,
                            PoseStrategy.CLOSEST_TO_REFERENCE_POSE, cam.offset));
        }
    }

    /**
     * Takes a swerve drive and adds pose estimate
     * @param swerveDrive The swerve drive.
     */
    public void addPoseEstimate(SwerveDrive swerveDrive) {

        int index = 0;
        List<EstimatedRobotPose> poses = new ArrayList<>();
        for (PhotonPoseEstimator estimate : poseEstimators) {
            if (cams.get(index).getLatestResult().hasTargets())
                poses.add(estimate.update(cams.get(index).getLatestResult()).get());
            index++;
        }

        // great now we have a estimate from all the cameras. I don't really know what
        // to do with this so i'll contruct an average pose i guess;
        double averageX = 0;
        double averageY = 0;
        double averageZ = 0;
        double averageRotationX = 0;// could this be an array? yes. will it be? no
        double averageRotationY = 0;
        double averageRotationZ = 0;
        double lastTimeStamp = 0;
        for (EstimatedRobotPose estimate : poses) {
            averageX += estimate.estimatedPose.getX();
            averageY += estimate.estimatedPose.getY();
            averageZ += estimate.estimatedPose.getZ();
            averageRotationX += estimate.estimatedPose.getRotation().getX();
            averageRotationY += estimate.estimatedPose.getRotation().getY();
            averageRotationZ += estimate.estimatedPose.getRotation().getZ();
            if (estimate.timestampSeconds > lastTimeStamp) {
                lastTimeStamp = estimate.timestampSeconds;
            }
        }
        averageX = averageX / cams.size();
        averageY = averageY / cams.size();
        averageZ = averageZ / cams.size();
        averageRotationX = averageRotationX / cams.size();
        averageRotationY = averageRotationY / cams.size();
        averageRotationZ = averageRotationZ / cams.size();
        Pose3d averagePose = new Pose3d(averageX, averageY, averageZ,
                new Rotation3d(averageRotationX, averageRotationY, averageRotationZ));// yay
        swerveDrive.addVisionMeasurement(
                new Pose2d(averagePose.getX(), averagePose.getY(), averagePose.getRotation().toRotation2d()),
                lastTimeStamp);// update the swerve drives position stuff
    }

    /**
     * Gets the closest tag to the front camera
     * @return An OptionalInt representing the id of the closest tagg if present.
     */
    public OptionalInt getClosestTagToFront() {

        PhotonPipelineResult result = cams.get(0).getLatestResult();
        if (!result.hasTargets())
            return OptionalInt.empty();

        Iterator<PhotonTrackedTarget> iterator = result.getTargets().iterator();
        PhotonTrackedTarget closest = iterator.next();

        while (iterator.hasNext()) {
            PhotonTrackedTarget target = iterator.next();
            if (target.getBestCameraToTarget().getX() < closest.getBestCameraToTarget().getX()) {
                closest = target;
            }
        }

        return OptionalInt.of(closest.fiducialId);
    }
}
