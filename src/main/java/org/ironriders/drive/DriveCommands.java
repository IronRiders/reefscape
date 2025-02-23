package org.ironriders.drive;

import java.lang.reflect.Field;
import java.util.OptionalInt;
import java.util.function.*;
import java.util.function.DoubleSupplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;

import java.util.function.Supplier;

import org.ironriders.lib.FieldUtils;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

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
		OptionalInt optID = driveSubsystem.getVision().getClosestTagToFront();
		if (!optID.isPresent())
			return Commands.none();

		int id = optID.getAsInt();
		if (!FieldUtils.isValidReefTag(id))
			return Commands.none();

		Pose2d basePose = FieldUtils.getPose(id);
		Pose2d robotPose = (offsetRight ? FieldUtils.REEFSIDE_RIGHT_OFFSET : FieldUtils.REEFSIDE_LEFT_OFFSET)
				.relativeTo(basePose);
		return this.driveToPose(robotPose);
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
		OptionalInt closestTag = driveSubsystem.getVision().getClosestTagToFront();
		if (closestTag.isPresent()) {
			return this.driveToPose(FieldUtils.getPose(closestTag.getAsInt()));
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

		// .until(() ->
		// (AutoBuilder.getCurrentPose().getTranslation().getDistance(targetPose.getTranslation())
		// <= 0.01)
		// &&
		// (AutoBuilder.getCurrentPose().getRotation().minus(targetPose.getRotation()).getDegrees()
		// <= 3.0));
	}
}
