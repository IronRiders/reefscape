package org.ironriders.vision;

import java.util.List;

import org.ironriders.core.Utils;
import org.ironriders.drive.DriveSubsystem;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import org.photonvision.*;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

public class VisionCommands {
    private final VisionSubsystem VisionSubsystem;
    private final DriveSubsystem driveSubsystem;
    AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

    public VisionCommands(VisionSubsystem visionSubsystem, DriveSubsystem driveSubsystem) {
        this.VisionSubsystem = visionSubsystem;
        this.driveSubsystem = driveSubsystem;
    }

    public Command alignCoral(PhotonCamera camera) {
        return VisionSubsystem.runOnce(() -> {
            print("started coral mini auto");
            int[] tags = null;
            if (!DriverStation.getAlliance().isPresent()) {
                error("no alliance!");
                return;
            }
            if (DriverStation.getAlliance().get() == Alliance.Red) {
                tags = VisionConstants.REEF_TAG_IDS_RED;
            } else {
                tags = VisionConstants.REEF_TAG_IDS_BLUE;
            }
            boolean moved = false;
            PhotonPipelineResult result = camera.getLatestResult();
            if (!result.hasTargets()) {
                error("no targets!");
                return;
            }
            
            for (int i : tags) {
                if (getPathToTag(i, result) != null) {
                    Translation2d path = getPathToTag(i, result).getTranslation().toTranslation2d();
                    while (path.getX() > 0 || path.getY() > 0) {
                        result = camera.getLatestResult();
                        if (!result.hasTargets()) {
                            print("no targets");
                            return;
                        } else {
                            List<PhotonTrackedTarget> targets = result.getTargets();
                            Boolean foundTag = false;
                            for (PhotonTrackedTarget target : targets) {
                                if (target.getFiducialId() == i) {
                                    foundTag = true;
                                }
                            }
                            if (!foundTag) {
                                print("lost target tag");
                                return;
                            }

                        }
                        if (getPathToTag(i, result) == null) {
                            continue;
                        }
                        path = getPathToTag(i, result).getTranslation().toTranslation2d();
                        double x = Utils.clamp(-.25, .25, -path.getX());
                        double y = Utils.clamp(-.25, .25, -path.getY());
                        driveSubsystem.drive(new Translation2d(x, y), 0, false);
                        print("x: " + x);
                        print("y: " + y);
                        moved = true;
                        if (DriverStation.isDisabled() || DriverStation.isEStopped()) {
                            print("stopped");
                            break;
                        }

                    }
                }
            }

        }

        );
    }

    private Transform3d getPathToTag(int id, PhotonPipelineResult result) {
        boolean hasTargets = result.hasTargets();
        if (!hasTargets) {
            print("no valid targets!");
            return null;
        }
        List<PhotonTrackedTarget> targets = result.getTargets();
        for (PhotonTrackedTarget target : targets) {
            if (target.getFiducialId() == id) {
                Transform3d pose = target.getBestCameraToTarget();
                return pose;
            }
        }
        return null;
    }

    private void print(String input) {
        System.out.println(input);
    }

    private void print(int input) {
        System.out.println(input);
    }

    private void print(boolean input) {
        System.out.println(input);
    }

    private void error(String input) {
        System.err.println(input);
    }
}