// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import static org.ironriders.wrist.coral.CoralWristConstants.FORWARD_LIMIT;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.climb.ClimbConstants;
import org.ironriders.climb.ClimbSubsystem;
import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.elevator.ElevatorSubsystem;
import org.ironriders.lib.GameState;
import org.ironriders.lib.RobotUtils;
import org.ironriders.lib.field.FieldElement.ElementType;
import org.ironriders.lib.field.FieldPose.Side;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.targeting.TargetingSubsystem;
import org.ironriders.wrist.algae.AlgaeIntakeCommands;
import org.ironriders.wrist.algae.AlgaeIntakeConstants;
import org.ironriders.wrist.algae.AlgaeIntakeSubsystem;
import org.ironriders.wrist.algae.AlgaeWristCommands;
import org.ironriders.wrist.algae.AlgaeWristConstants;
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
import edu.wpi.first.wpilibj2.command.Commands;
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

	private final SendableChooser<Command> autoChooser;

	private final CommandXboxController primaryController = new CommandXboxController(
			DriveConstants.PRIMARY_CONTROLLER_PORT);
	private final CommandGenericHID secondaryController = new CommandJoystick(DriveConstants.KEYPAD_CONTROLLER_PORT);
	private double inversionCoeff = 1;

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
								inversionCoeff*primaryController.getLeftY()*driveSubsystem.ControlSpeedMultipler,
								DriveConstants.TRANSLATION_CONTROL_EXPONENT,
								DriveConstants.TRANSLATION_CONTROL_DEADBAND),
						() -> RobotUtils.controlCurve(
								inversionCoeff*primaryController.getLeftX()*driveSubsystem.ControlSpeedMultipler,
								DriveConstants.TRANSLATION_CONTROL_EXPONENT,
								DriveConstants.TRANSLATION_CONTROL_DEADBAND),
						() -> RobotUtils.controlCurve(
								inversionCoeff*primaryController.getRightX()*driveSubsystem.ControlSpeedMultipler,
								DriveConstants.ROTATION_CONTROL_EXPONENT,
								DriveConstants.ROTATION_CONTROL_DEADBAND)));

		// slows down drivetrain when pressed
		primaryController.leftTrigger().onTrue(driveCommands.setDriveTrainSpeed(true)).onFalse(driveCommands.setDriveTrainSpeed(false));

		// jog commands on pov buttons
		for (var angle = 0; angle < 360; angle += 45) {
			primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
		}
		// y vision align station not implmented yet //TODO
		// x vision align reef not implmented yet //TODO

		primaryController.y().onTrue(targetingCommands.targetNearest(ElementType.STATION).andThen(driveCommands.pathfindToTarget()));
		primaryController.x().onTrue(targetingCommands.targetNearest(ElementType.REEF).andThen(driveCommands.pathfindToTarget()));
    
		primaryController.button(5).onTrue(driveCommands.jog(90.0));
		primaryController.button(6).onTrue(driveCommands.jog(270.0));



		//Secondary Driver left side buttons
		secondaryController.button(1).whileTrue(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.EJECT)).whileFalse(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.STOP));
		secondaryController.button(2).whileTrue(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.GRAB)).whileFalse(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.STOP));
		secondaryController.button(11).whileTrue(algaeIntakeCommands.set(AlgaeIntakeState.GRAB)).whileFalse(algaeIntakeCommands.set(AlgaeIntakeState.STOP));
		secondaryController.button(16).whileTrue(algaeIntakeCommands.set(AlgaeIntakeState.EJECT)).whileFalse(algaeIntakeCommands.set(AlgaeIntakeState.STOP));

		secondaryController.button(5).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L1));
		secondaryController.button(6).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L2));
		secondaryController.button(7).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L3));
		secondaryController.button(8).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L4));
		secondaryController.button(9).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.CoralStation));
		secondaryController.button(10).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.Down));
		secondaryController.pov(0).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.HighAlgae));
		
		//right side buttons
		secondaryController.button(4).onTrue(algaeWristCommands.set(AlgaeWristState.EXTENDED));
		secondaryController.button(3).onTrue(algaeWristCommands.set(AlgaeWristState.STOWED));

		secondaryController.button(14).whileTrue(climbCommands.set(ClimbConstants.State.UP)).whileFalse(climbCommands.set(ClimbConstants.State.STOP));
		secondaryController.button(15).whileTrue(climbCommands.set(ClimbConstants.State.DOWN)).whileFalse(climbCommands.set(ClimbConstants.State.STOP));

		
		secondaryController.axisGreaterThan(1, -.5).whileFalse(algaeIntakeCommands.set(AlgaeIntakeState.EJECT)).whileTrue(algaeIntakeCommands.set(AlgaeIntakeState.STOP));
		secondaryController.axisLessThan(1, .5).whileFalse(algaeIntakeCommands.set(AlgaeIntakeState.GRAB)).whileTrue(algaeIntakeCommands.set(AlgaeIntakeState.STOP));

	}

	/**
	 * @return the command to run in autonomous
	 */
	public Command getAutonomousCommand() {
		return autoChooser.getSelected();
	}
}
