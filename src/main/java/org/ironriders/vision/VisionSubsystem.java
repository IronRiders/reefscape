package org.ironriders.vision;

import java.util.ArrayList;
import java.util.List;

import org.ironriders.drive.DriveSubsystem;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {
    private VisionCommands commands;
    private PhotonCamera camera = new PhotonCamera(VisionConstants.CAM_NAME);
    private DriveSubsystem driveSubsystem;
    public boolean canAlignCoral;// you can get this if you want!

    public VisionSubsystem(DriveSubsystem driveSubsystem) {
        this.driveSubsystem = driveSubsystem;
        this.commands = new VisionCommands(this, driveSubsystem);
    }

    /** Fetch the VisionCommands instance */
    public VisionCommands getCommands() {
        return commands;
    }

    public PhotonCamera getCamera() {
        return this.camera;
    }

    @Override
    public void periodic() {
        PhotonPipelineResult result = camera.getLatestResult();
        if (!result.hasTargets()) {
            SmartDashboard.putString("IDs", "None");
            return;
        }
        List<PhotonTrackedTarget> targets = result.getTargets();
        String ids = "IDs: ";
        for (PhotonTrackedTarget target : targets) {
            ids += (target.fiducialId) + " ";
        }
        SmartDashboard.putString("IDs", ids);
        Field2d m_field = new Field2d();
        SmartDashboard.putData("pose", m_field);
        m_field.setRobotPose(driveSubsystem.getSwerveDrive().getPose());

        if (VisionConstants.CAM_OFFSETS.length == 0) {
            return;
        }
        // this has to be changed to our custom field for testing
        AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);
        List<PhotonCamera> cams = new ArrayList<>();
        for (String name : VisionConstants.CAM_NAMES) {
            cams.add(new PhotonCamera(name));
        }
        if (cams.size() != VisionConstants.CAM_OFFSETS.length) {
            System.out.print("VISION ARRAY MISMATCH!!!!!!!");
            return;
        }
        List<PhotonPoseEstimator> poseEstimators = new ArrayList<>();
        for (Transform3d offsett : VisionConstants.CAM_OFFSETS) {
            poseEstimators
                    .add(new PhotonPoseEstimator(aprilTagFieldLayout, PoseStrategy.CLOSEST_TO_REFERENCE_POSE, offsett));
        }
        int index = 0;
        List<EstimatedRobotPose> poses = new ArrayList<>();
        for (PhotonPoseEstimator estimate : poseEstimators) {
            result = cams.get(index).getLatestResult();
            poses.add(estimate.update(result).get());
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
        driveSubsystem.getSwerveDrive().addVisionMeasurement(
                new Pose2d(averagePose.getX(), averagePose.getY(), averagePose.getRotation().toRotation2d()),
                lastTimeStamp);// update the swerve drives position stuff
    }
}
