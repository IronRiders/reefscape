// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.climb.ClimbConstants;
import org.ironriders.climb.ClimbSubsystem;
import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.elevator.ElevatorSubsystem;
import org.ironriders.lib.RobotUtils;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.targeting.TargetingSubsystem;
import org.ironriders.wrist.algae.AlgaeIntakeCommands;
import org.ironriders.wrist.algae.AlgaeIntakeSubsystem;
import org.ironriders.wrist.algae.AlgaeWristCommands;
import org.ironriders.wrist.algae.AlgaeWristSubsystem;
import org.ironriders.wrist.algae.AlgaeIntakeConstants.AlgaeIntakeState;
import org.ironriders.wrist.algae.AlgaeWristConstants.AlgaeWristState;
import org.ironriders.wrist.coral.CoralIntakeCommands;
import org.ironriders.wrist.coral.CoralIntakeConstants;
import org.ironriders.wrist.coral.CoralIntakeSubsystem;
import org.ironriders.wrist.coral.CoralWristCommands;
import org.ironriders.wrist.coral.CoralWristSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

	// The robot's subsystems and commands are defined here...
	public final DriveSubsystem driveSubsystem = new DriveSubsystem();
	public final DriveCommands driveCommands = driveSubsystem.getCommands();

	public final TargetingSubsystem targetingSubsystem = new TargetingSubsystem();
	public final TargetingCommands targetingCommands = targetingSubsystem.getCommands();

	public final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
	public final ElevatorCommands elevatorCommands = elevatorSubsystem.getCommands();

	public final CoralWristSubsystem coralWristSubsystem = new CoralWristSubsystem();
	public final CoralWristCommands coralWristCommands = coralWristSubsystem.getCommands();

	public final CoralIntakeSubsystem coralIntakeSubsystem = new CoralIntakeSubsystem();
	public final CoralIntakeCommands coralIntakeCommands = coralIntakeSubsystem.getCommands();

	public final AlgaeWristSubsystem algaeWristSubystem = new AlgaeWristSubsystem();
	public final AlgaeWristCommands algaeWristCommands = algaeWristSubystem.getCommands();

	public final AlgaeIntakeSubsystem algaeIntakeSubsystem = new AlgaeIntakeSubsystem();
	public final AlgaeIntakeCommands algaeIntakeCommands = algaeIntakeSubsystem.getCommands();

	public final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
	public final ClimbCommands climbCommands = climbSubsystem.getCommands();

	/**
	 * A chooser for selecting autonomous commands.
	 * This allows the user to select different autonomous routines
	 * from the dashboard.
	 */
	private final SendableChooser<Command> autoChooser;

	private final CommandXboxController primaryController = new CommandXboxController(
			DriveConstants.PRIMARY_CONTROLLER_PORT);
	private final CommandGenericHID secondaryController = new CommandJoystick(
			DriveConstants.KEYPAD_CONTROLLER_PORT);

	public final RobotCommands robotCommands = new RobotCommands(
			driveCommands, targetingCommands, elevatorCommands,
			coralWristCommands, coralIntakeCommands,
			algaeWristCommands, algaeIntakeCommands,
			climbCommands,
			primaryController.getHID());

	/**
	 * The container for the robot. Contains subsystems, IO devices, and commands.
	 */

	public RobotContainer() {
		// Configure the trigger bindings
		configureBindings();

		autoChooser = AutoBuilder.buildAutoChooser();
		SmartDashboard.putData("Auto Select", autoChooser);
	}

	private void configureBindings() {

		// DRIVE CONTROLS
		driveSubsystem.setDefaultCommand(
				robotCommands.driveTeleop(
						() -> RobotUtils.controlCurve(
								-primaryController.getLeftY(),
								DriveConstants.TRANSLATION_CONTROL_EXPONENT,
								DriveConstants.TRANSLATION_CONTROL_DEADBAND),
						() -> RobotUtils.controlCurve(
								-primaryController.getLeftX(),
								DriveConstants.TRANSLATION_CONTROL_EXPONENT,
								DriveConstants.TRANSLATION_CONTROL_DEADBAND),
						() -> RobotUtils.controlCurve(
								-primaryController.getRightX(),
								DriveConstants.ROTATION_CONTROL_EXPONENT,
								DriveConstants.ROTATION_CONTROL_DEADBAND)));

		// slows down drivetrain when pressed
		primaryController.leftTrigger().onTrue(driveCommands.setDriveTrainSpeed(true))
				.onFalse(driveCommands.setDriveTrainSpeed(false));

		// elevator commands via bumpers TODO: ask how to make the bumper commands cleaner

		// sets the elevator command to 0.1 when the right bumper is pressed and the left bumper is not pressed.
		primaryController.rightBumper().and(() -> !primaryController.leftBumper().getAsBoolean())
				.whileTrue(climbCommands.setMotor(DriveConstants.MOTOR_DOWN_SPEED));
		
		// sets the elevator command to -0.1 when the left bumper is pressed and the right bumper is not pressed.
		primaryController.leftBumper().and(() -> !primaryController.rightBumper().getAsBoolean())
				.whileTrue(climbCommands.setMotor(DriveConstants.MOTOR_UP_SPEED));
		
		// sets the elevator command to 0.0 when both bumpers are pressed.
		primaryController.rightBumper().and(() -> primaryController.leftBumper().getAsBoolean())
				.whileTrue(climbCommands.setMotor(0.0));
		
		// sets the elevator command to 0.0 when neither bumpers are not pressed.
		primaryController.leftBumper().or(() -> primaryController.leftBumper().getAsBoolean())
				.whileFalse(climbCommands.setMotor(0.0));

		// jog commands on pov buttons
		for (var angle = 0; angle < 360; angle += 45) {
			primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
		}

		// y vision align station not implimented yet //TODO
		// x vision align reef not implimented yet //TODO
		// Secondary Driver left side buttons
		secondaryController.button(1).whileTrue(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.EJECT))
				.whileFalse(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.STOP));
		secondaryController.button(2).whileTrue(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.GRAB))
				.whileFalse(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.STOP));
		secondaryController.button(3).whileTrue(algaeIntakeCommands.set(AlgaeIntakeState.GRAB))
				.whileFalse(algaeIntakeCommands.set(AlgaeIntakeState.STOP));
		secondaryController.button(4).whileTrue(algaeIntakeCommands.set(AlgaeIntakeState.EJECT))
				.whileFalse(algaeIntakeCommands.set(AlgaeIntakeState.STOP));

		secondaryController.button(5).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L1));
		secondaryController.button(6).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L2));
		secondaryController.button(7).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L3));
		secondaryController.button(8).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L4));
		secondaryController.button(9).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.CoralStation));
		secondaryController.button(10).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.Down));

		// right side buttons
		secondaryController.button(11).onTrue(algaeWristCommands.set(AlgaeWristState.EXTENDED));
		secondaryController.button(12).onTrue(algaeWristCommands.set(AlgaeWristState.STOWED));
		secondaryController.button(13).onTrue(climbCommands.goTo(ClimbConstants.Targets.MAX));
		secondaryController.button(14).onTrue(climbCommands.goTo(ClimbConstants.Targets.TARGET));
	}

	/**
	 * @return the command to run in autonomous
	 */
	public Command getAutonomousCommand() {
		return autoChooser.getSelected();
	}
}
