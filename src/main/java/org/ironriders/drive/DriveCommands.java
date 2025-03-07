package org.ironriders.drive;

import java.util.OptionalInt;
import java.util.function.*;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;

import org.ironriders.lib.FieldUtils;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

@Logged
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

	public Command driveTeleop(DoubleSupplier inputTranslationX, DoubleSupplier inputTranslationY,
			DoubleSupplier inputRotation, boolean fieldRelative) {
		if (DriverStation.isAutonomous())
			return Commands.none();

		double invert = DriverStation.getAlliance().isEmpty() || DriverStation.getAlliance().get() == DriverStation.Alliance.Blue
			? 1 : -1;

		return drive(
				() -> new Translation2d(inputTranslationX.getAsDouble(), inputTranslationY.getAsDouble())
						.times(DriveConstants.SWERVE_DRIVE_MAX_SPEED)
						.times(invert),
				() -> inputRotation.getAsDouble() * DriveConstants.SWERVE_DRIVE_MAX_SPEED * invert,
				() -> fieldRelative);
	}

	public Command jog(double robotRelativeAngleDegrees) {
		// Note - PathFinder does not do well with small moves so we move manually

		// Compute distance to travel (TODO - distance is slightly fictional without PID control)
		var distance = Units.inchesToMeters(DriveConstants.JOG_DISTANCE_INCHES);

		// Compute velocity
		var vector = new Translation2d(
			distance,
			Rotation2d.fromDegrees(robotRelativeAngleDegrees)
		);
		var scale = Math.max(Math.abs(vector.getX()), Math.abs(vector.getY())) / DriveConstants.JOG_SPEED;
		var velocity = vector.div(scale);

		return driveSubsystem.runOnce(() -> {
			System.out.println("Jogging " + robotRelativeAngleDegrees + "° (robot relative)");
			var startPosition = driveSubsystem.getPose().getTranslation();

			driveTeleop(velocity::getX, velocity::getY, () -> 0, false)
				.repeatedly()	
				.until(() -> driveSubsystem.getPose().getTranslation().getDistance(startPosition) > distance
				)
				.schedule();
		});
	}

	// aligns to the closest visible side of the reef
	public Command alignToReef(boolean offsetRight) {
		return driveSubsystem.defer(() -> {
			// OptionalInt optID = driveSubsystem.getVision().getCamera("frontRight").getClosestVisible();
			// if (!optID.isPresent())
			// 	return Commands.none();

			// int id = optID.getAsInt();
			// if (!FieldUtils.isValidReefTag(id))
			// return Commands.none();

			Pose2d basePose = FieldUtils.getPose(13);
			Pose2d robotPose = new Pose2d(
				//basePose.getTranslation(), 
				driveSubsystem.getPose().getTranslation(),
				driveSubsystem.getPose().getRotation())
				.transformBy(new Transform2d(new Translation2d(1.0, 0), new Rotation2d(Math.PI / 2)));

			System.out.println("BASE POSE: " + basePose);
			System.out.println("ROBOT POSE: " + robotPose);
			System.out.println("OFFSET POSE: " + basePose.transformBy(FieldUtils.REEFSIDE_LEFT_OFFSET));
			System.out.println("CURRENT POSE: " + driveSubsystem.getSwerveDrive().getPose());

			return this.pathfindToPose(robotPose);
		});
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
		return driveSubsystem.defer(() -> {
			OptionalInt closestTag = driveSubsystem.getVision().getCamera("front").getClosestVisible();
			if (closestTag.isPresent()) {
				return this.pathfindToPose(FieldUtils.getPose(closestTag.getAsInt()));
			} else {
				return Commands.none();
			}
		});
	}

	public Command pathfindToPose(Pose2d targetPose) {
		return AutoBuilder.pathfindToPose(targetPose, new PathConstraints(
				DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO,
				DriveConstants.SWERVE_MAXIMUM_ACCELERATION_AUTO,
				DriveConstants.SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO,
				DriveConstants.SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO));
	}
}
