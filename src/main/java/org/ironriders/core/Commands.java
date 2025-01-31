package org.ironriders.core;

import java.util.function.DoubleSupplier;

import org.ironriders.drive.DriveSubsystem;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import swervelib.SwerveDrive;

public class Commands {
	
	private final DriveSubsystem driveSubsystem;

	public Commands(DriveSubsystem driveSubsystem) {
		this.driveSubsystem = driveSubsystem;
	}


	/**
	 * Command to drive the robot given controller input.
	 * 
	 * @param inputTranslationX DoubleSupplier, value from 0-1.
	 * @param inputTranslationY DoubleSupplier, value from 0-1.
	 * @param inputRotation     DoubleSupplier, value from 0-1.
	 */
	public Command driveTeleop(DoubleSupplier inputTranslationX, DoubleSupplier inputTranslationY,
			DoubleSupplier inputRotation) {
		return driveSubsystem.runOnce(() -> {
			// No driver input while autonomous
			if (DriverStation.isAutonomous())
				return;

			SwerveDrive swerveDrive = driveSubsystem.getSwerveDrive();

			// Run the drive method with the inputs multiplied by the max speed.
			driveSubsystem.drive(
					new Translation2d(
							inputTranslationX.getAsDouble() * swerveDrive.getMaximumChassisVelocity(),
							inputTranslationY.getAsDouble() * swerveDrive.getMaximumChassisVelocity()),
					inputRotation.getAsDouble() * swerveDrive.getMaximumChassisAngularVelocity(),
					true
			);
		});
	}
}
