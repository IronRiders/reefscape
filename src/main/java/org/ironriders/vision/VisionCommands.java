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

            System.out.println("started coral mini auto");
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
                    Translation2d path = getPathToTag(i, result);
                    while (path.getX() > 0 || path.getY() > 0) {
                    path = getPathToTag(i, result);
                        print("running drive");
                        driveSubsystem.drive(path, 0, false);
                        print("ran drive");
                        print("x:" + path.getX());
                        print("y:" + path.getY());
                        moved = true;
                    }
                }

            }
            if (!moved) {
                print("no tags I want");
                List<PhotonTrackedTarget> targets = result.getTargets();
                for (PhotonTrackedTarget target : targets) { // this whole if statement is really just for debugging and
                                                             // is probably outdated due to the smart dashboard layout
                    print(target.fiducialId);// my experiance however is that as soon as i remove it i'll need it so it
                                             // stays
                }
                return;
            }

        }

        );
    }

    private Translation2d getPathToTag(int id, PhotonPipelineResult result) {
        boolean hasTargets = result.hasTargets();
        if (!hasTargets) {
            print("no valid targets!"); // this should be caught before this function is ran but might as well double
                                              // check
            return null;
        }
        List<PhotonTrackedTarget> targets = result.getTargets();
        for (PhotonTrackedTarget target : targets) {// see comment on nested for loops in visionSubsystem
            if (target.getFiducialId() == id) {
                Transform3d pose = target.getBestCameraToTarget();
                return new Translation2d(pose.getX(), pose.getY());
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

    private void error(String input) {
        System.err.println(input);
    }
}