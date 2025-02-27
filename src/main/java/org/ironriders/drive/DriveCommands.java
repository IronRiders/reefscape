package org.ironriders.drive;

import java.util.OptionalInt;
import java.util.function.*;
import java.util.function.DoubleSupplier;


import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import java.util.function.Supplier;

import org.ironriders.core.FieldConstants;
import org.PathPlannerPath;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Commands;
import swervelib.SwerveDrive;

public class DriveCommands {
	private final DriveSubsystem driveSubsystem;

	public DriveCommands(DriveSubsystem driveSubsystem) {
		this.driveSubsystem = driveSubsystem;
	}

	public Command drive(Supplier<Translation2d> translation, DoubleSupplier rotation, BooleanSupplier fieldRelative) {
		return driveSubsystem.runOnce(() -> {

			driveSubsystem.drive(translation.get(), rotation.getAsDouble(), fieldRelative.getAsBoolean());
		});
	}

	// aligns to the closest visible side of the reef
	public Command alignToReef(boolean offsetRight) {
		// TODO
		return Commands.none();
	}

	public Command alignToStation() {
		// TODO
		return Commands.none();
	}

	public Command alignToProcessor() {
		// TODO
		return Commands.none();
	}

	public Command alignToBarge() {
		// TODO
		return Commands.none();
	}

	public Command alignToClosestTag() {
		OptionalInt closestTag = driveSubsystem.getVision().getClosestTag();
		if (closestTag.isPresent()) {
			return this.driveToPose(FieldConstants.getPose(closestTag.getAsInt()));
		} else {
			return Commands.none();
		}
	}

	public Command driveToPose(Pose2d targetPose) {
		return AutoBuilder.pathfindToPose(targetPose, new PathConstraints(
				DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO,
				DriveConstants.SWERVE_MAXIMUM_ACCELERATION_AUTO, 
				DriveConstants.SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO, 
				DriveConstants.SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO));
	}

	public Command runPath(String Path) {
    try{
        // Load the path you want to follow using its name in the GUI
        PathPlannerPath autoPath = PathPlannerPath.fromPathFile(Path);
		System.out.println(Path);
        // Create a path following command using AutoBuilder. This will also trigger event markers.
        return AutoBuilder.followPath(autoPath);
    } catch (Exception e) {
		System.out.println("Error while following path: ".concat(Path));
        DriverStation.reportError("Error" + e.getMessage(), e.getStackTrace());
        return Commands.none();
		
    }
  }
}