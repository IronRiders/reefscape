package org.ironriders.drive;

import java.util.function.*;
import java.util.function.DoubleSupplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import java.util.function.Supplier;
import edu.wpi.first.wpilibj2.command.Command;

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

	public Command driveToPose(Pose2d targetPose) {
		return AutoBuilder.pathfindToPose(targetPose, new PathConstraints(
				DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO,
				DriveConstants.SWERVE_MAXIMUM_ACCELERATION_AUTO, 
				DriveConstants.SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO, 
				DriveConstants.SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO));
	}
}