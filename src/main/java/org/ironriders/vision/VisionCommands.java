package org.ironriders.vision;

import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;

public class VisionCommands {
    @SuppressWarnings("unused")
    private final VisionSubsystem VisionSubsystem;
    @SuppressWarnings("unused")
    private final DriveSubsystem driveSubsystem;
    AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

    public VisionCommands(VisionSubsystem visionSubsystem, DriveSubsystem driveSubsystem) {
        this.VisionSubsystem = visionSubsystem;
        this.driveSubsystem = driveSubsystem;
    }

    /**
     * Get a command to a given coral station
     * 
     * @param station the coral station you want to go to. it will automaticlly
     *                select your alliance.
     * @return a command to move to the station on success or a blank command on
     *         error
     */
    public Command alignCoral(int station) {
        Pose2d[] locations;
        if (DriverStation.getAlliance().get() == Alliance.Blue) {
            locations = VisionConstants.STATION_LOCATIONS_BLUE;
        } else {
            if (DriverStation.getAlliance().get() == Alliance.Red) {
                locations = VisionConstants.STATION_LOCATIONS_RED;
            } else {
                print("no alliance set");
                return new Command() {
                };
            }
        }
        Pose2d location = locations[station];
        return AutoBuilder.pathfindToPose(location, new PathConstraints(DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO,
                DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO / 2, 10, 2));
    }

    private void print(String input) {
        System.out.println(input);
    }

    @SuppressWarnings("unused")
    private void print(int input) {
        System.out.println(input);
    }

    @SuppressWarnings("unused")
    private void print(boolean input) {
        System.out.println(input);
    }
}