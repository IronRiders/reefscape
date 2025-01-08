package org.ironriders.vision;

import java.util.List;

import org.ironriders.drive.DriveSubsystem;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import org.photonvision.*;
import org.photonvision.targeting.PhotonTrackedTarget;

public class VisionCommands {
    private final VisionSubsystem VisionSubsystem;
    private final DriveSubsystem driveSubsystem;
    PhotonCamera camera = new PhotonCamera(VisionConstants.CAM_NAME);
    AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

    public VisionCommands(VisionSubsystem visionSubsystem, DriveSubsystem driveSubsystem) {
        this.VisionSubsystem = visionSubsystem;
        this.driveSubsystem = driveSubsystem;
    }

    public Command alignCoral() {
        return VisionSubsystem.runOnce(() -> {
            int[] tags = null;
            if (!DriverStation.getAlliance().isPresent()) {
                return;
            }
            if (DriverStation.getAlliance().get() == Alliance.Red) {
                tags = VisionConstants.REEF_TAG_IDS_RED;
            } else {
                tags = VisionConstants.REEF_TAG_IDS_BLUE;
            }
            for (int i : tags) {
                if (getPathToTag(i) != null) {
                    Translation2d path = getPathToTag(i);
                    driveSubsystem.drive(path, 0, true);
                }
            }
        }

        );
    }

    private Translation2d getPathToTag(int id) {
        var result = camera.getLatestResult();
        boolean hasTargets = result.hasTargets();
        if (!hasTargets) {
            return null;
        }
        List<PhotonTrackedTarget> targets = result.getTargets();
        for (PhotonTrackedTarget target : targets) {
            if (target.getFiducialId() == id) {
                Transform3d pose = target.getBestCameraToTarget();
                return new Translation2d(pose.getX(), pose.getY());
            }
        }
        return null;
    }
}
