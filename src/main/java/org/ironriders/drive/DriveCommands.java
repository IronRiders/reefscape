package org.ironriders.drive;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.ironriders.core.RobotContainer;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathPlannerPath;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import swervelib.SwerveDrive;

public class DriveCommands {
	private final DriveSubsystem driveSubsystem;
	private final SwerveDrive swerveDrive;

	public DriveCommands(DriveSubsystem driveSubsystem) {
		this.driveSubsystem = driveSubsystem;
		this.swerveDrive = driveSubsystem.getSwerveDrive();
	}

	/*
	 * SparkMax sparkMax=new SparkMax(13, MotorType.kBrushless);
	 * public Command driveTest(){
	 * return driveSubsystem.runOnce(()->{
	 * sparkMax.set(1);
	 * }
	 * );
	 * }
	 */
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

			// Run the drive method with the inputs multiplied by the max speed.
			driveSubsystem.drive(
					new Translation2d(
			// inputTranslationX.getAsDouble() * swerveDrive.getMaximumChassisVelocity(),
			// inputTranslationY.getAsDouble() * swerveDrive.getMaximumChassisVelocity()
			),
					// inputRotation.getAsDouble() * swerveDrive.getMaximumChassisAngularVelocity(),
					0,
					true // Gus likes it this way
			);
		});
	}

	public Command runPath(String Path) {
    try{
        // Load the path you want to follow using its name in the GUI
        PathPlannerPath autoPath = PathPlannerPath.fromPathFile(Path);
		System.out.println(Path);
        // Create a path following command using AutoBuilder. This will also trigger event markers.
        return AutoBuilder.followPath(autoPath);
    } catch (Exception e) {
		System.out.println("eroor");
        DriverStation.reportError("Something bad happen !!: " + e.getMessage(), e.getStackTrace());
        return Commands.none();
		
    }
  }
}
