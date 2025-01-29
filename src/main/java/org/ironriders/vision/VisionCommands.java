package org.ironriders.vision;

import org.ironriders.drive.DriveSubsystem;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import org.photonvision.*;

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

    public Command alignCoral(PhotonCamera camera,int station) {
        Pose2d[] locations;
         if(DriverStation.getAlliance().get() ==Alliance.Blue){
            locations=VisionConstants.STATION_LOCATIONS_BLUE;
         }else{
            if(DriverStation.getAlliance().get()==Alliance.Red){
                locations=VisionConstants.STATION_LOCATIONS_RED;
            }
            else{
                print("no alliance set");
                return null;
            }
         }
        Pose2d location=locations[station];
        return AutoBuilder.pathfindToPose(location, new PathConstraints(.5,.25,10,2));
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
}