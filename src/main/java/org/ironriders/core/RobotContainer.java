// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;

import java.lang.ModuleLayer.Controller;

import org.ironriders.algae.AlgaeIntakeCommands;
import org.ironriders.algae.AlgaeIntakeSubsystem;
import org.ironriders.algae.AlgaeWristCommands;
import org.ironriders.algae.AlgaeWristConstants;
import org.ironriders.algae.AlgaeWristSubsystem;
import org.ironriders.coral.CoralIntakeCommands;
import org.ironriders.coral.CoralIntakeConstants;
import org.ironriders.coral.CoralIntakeSubsystem;
import org.ironriders.coral.CoralWristCommands;
import org.ironriders.coral.CoralWristConstants;
import org.ironriders.coral.CoralWristSubsystem;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorSubsystem;
import org.ironriders.elevator.ElevatorConstants;
//import org.ironriders.manipulators.CoralWristCommands;
//import org.ironriders.manipulators.CoralWristSubsystem;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import org.ironriders.vision.Vision;
import org.photonvision.PhotonCamera;

import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import org.ironriders.vision.VisionCommands;
import org.ironriders.vision.VisionSubsystem;
import org.photonvision.PhotonCamera;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
//import edu.wpi.*;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

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
	private final Vision vision = new Vision();

	private final DriveSubsystem driveSubsystem = new DriveSubsystem(vision);
	private final DriveCommands driveCommands = driveSubsystem.getCommands();

	public final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
	public final ElevatorCommands elevatorCommands = elevatorSubsystem.getCommands();

	private final CoralWristSubsystem coralWristSubsystem = new CoralWristSubsystem();
	public final CoralWristCommands coralWristCommands = coralWristSubsystem.getCommands();

	private final CoralIntakeSubsystem coralIntakeSubsystem = new CoralIntakeSubsystem();
	private final CoralIntakeCommands coralIntakeCommands = coralIntakeSubsystem.getCommands();

	private final AlgaeWristSubsystem algaeWristSubystem = new AlgaeWristSubsystem();
	private final AlgaeWristCommands algaeWristCommands = algaeWristSubystem.getCommands();

	// private final AlgaeIntakeSubsystem algaeIntakeSubsystem = new AlgaeIntakeSubsystem();
	// private final AlgaeIntakeCommands algaeIntakeCommands = algaeIntakeSubsystem.getCommands();

	private final SendableChooser<Command> autoChooser;

	private final CommandXboxController primaryController = new CommandXboxController(DriveConstants.PRIMARY_CONTROLLER_PORT);
	private final CommandGenericHID secondaryController = new CommandGenericHID(DriveConstants.KEYPAD_CONTROLLER_PORT);
	private final CommandXboxController tertiaryController = new CommandXboxController(1);

	private final RobotCommands robotCommands = new RobotCommands(
			driveCommands, elevatorCommands, 

			coralWristCommands, coralIntakeCommands, 
			algaeWristCommands, 
			// algaeIntakeCommands, 
			 primaryController.getHID());


	// non-final variables
	private ElevatorConstants.Level coralTarget = ElevatorConstants.Level.L1; // for scoring coral
	private ElevatorConstants.Level algaeTarget = ElevatorConstants.Level.L3; // for grabbing algae

	/**
	 * The container for the robot. Contains subsystems, IO devices, and commands.
	 */
	public RobotContainer() {
		// Configure the trigger bindings
		configureBindings();

		autoChooser = AutoBuilder.buildAutoChooser();
		SmartDashboard.putData("Auto Select", autoChooser);
	}

	/**
	 * Use this method to define your trigger->command mappings. Triggers can be
	 * created via the
	 * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
	 * an arbitrary
	 * predicate, or via the named factories in {@link
	 * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
	 * {@link
	 * CommandXboxController
	 * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
	 * PS4} controllers or
	 * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
	 * joysticks}.
	 */
	private void configureBindings() {

		// drive controls based on cubic function-ified joystick values
		driveSubsystem.setDefaultCommand(
				robotCommands.driveTeleop(
						() -> Utils.controlCurve(
								primaryController.getLeftY(),
								DriveConstants.TRANSLATION_CONTROL_EXPONENT,
								DriveConstants.TRANSLATION_CONTROL_DEADBAND),
						() -> Utils.controlCurve(
								primaryController.getLeftX(),
								DriveConstants.TRANSLATION_CONTROL_EXPONENT,
								DriveConstants.TRANSLATION_CONTROL_DEADBAND),
						() -> Utils.controlCurve(
								-primaryController.getRightX(),
								DriveConstants.ROTATION_CONTROL_EXPONENT,
								DriveConstants.ROTATION_CONTROL_DEADBAND)));

		// 1234 -> coral target controls
		// 3344 -> algae target controls
		// OOOO }__ not used yet
		// OOOO }/
		// secondary controls
		// secondaryController.button(0).onTrue(Commands.runOnce(() -> { coralTarget = ElevatorConstants.Level.L1; }));
		// secondaryController.button(1).onTrue(Commands.runOnce(() -> { coralTarget = ElevatorConstants.Level.L2; }));
		// secondaryController.button(2).onTrue(Commands.runOnce(() -> { coralTarget = ElevatorConstants.Level.L3; }));
		// secondaryController.button(3).onTrue(Commands.runOnce(() -> { coralTarget = ElevatorConstants.Level.L4; }));

		// secondaryController.button(4).onTrue(Commands.runOnce(() -> { algaeTarget = ElevatorConstants.Level.L3; }));
		// secondaryController.button(6).onTrue(Commands.runOnce(() -> { algaeTarget = ElevatorConstants.Level.L4; }));

		// // various scoring controls and such (bumper for coral, trigger for algae, rightside for score, lefside for grab)
		// primaryController.rightBumper().onTrue(robotCommands.prepareToScoreAlgae());
		// primaryController.rightBumper().onFalse(robotCommands.scoreAlgae());

		// primaryController.rightTrigger().onTrue(robotCommands.prepareToScoreCoral(coralTarget));
		// primaryController.rightTrigger().onFalse(robotCommands.scoreCoral());

		// primaryController.leftBumper().onTrue(robotCommands.prepareToGrabAlgae(algaeTarget));
		// primaryController.leftBumper().onFalse(robotCommands.grabAlgae());

		// primaryController.leftTrigger().onTrue(robotCommands.prepareToGrabCoral());
		// primaryController.leftTrigger().onFalse(robotCommands.grabCoral());

		// // primaryController.y().onTrue(elevatorCommands.set(ElevatorConstants.Level.L1));
		// primaryController.rightBumper().onTrue(elevatorCommands.set(ElevatorConstants.Level.L1));
		// primaryController.leftBumper().onTrue(elevatorCommands.set(ElevatorConstants.Level.L2));
		// // primaryController.rightTrigger().onTrue(elevatorCommands.set(ElevatorConstants.Level.L4));
		// // primaryController.a().onTrue(coralWristCommands.set(CoralWristConstants.State.STATION));
		// primaryController.b().onTrue(coralWristCommands.set(CoralWristConstants.State.L1toL3));
		// primaryController.a().onTrue(coralWristCommands.set(CoralWristConstants.State.L4));
		// primaryController.leftTrigger().onTrue(coralWristCommands.set(CoralWristConstants.State.L4));
		// primaryController.x().onTrue(coralWristCommands.set(CoralWristConstants.State.STOWED));
		// primaryController.y().onTrue(coralIntakeCommands.set(CoralIntakeConstants.State.STOP));
		// primaryController.leftTrigger().onTrue(coralIntakeCommands.set(CoralIntakeConstants.State.EJECT));
		// // primaryController.rightTrigger().onTrue(coralIntakeCommands.set(CoralIntakeConstants.State.GRAB))
		primaryController.a().onTrue(algaeWristCommands.set(AlgaeWristConstants.State.EXTENDED));
		primaryController.b().onTrue(algaeWristCommands.set(AlgaeWristConstants.State.STOWED));

		primaryController.rightTrigger().onTrue(coralIntakeCommands.set(CoralIntakeConstants.State.EJECT));
		primaryController.leftTrigger().onTrue(coralIntakeCommands.set(CoralIntakeConstants.State.GRAB));
		primaryController.leftBumper().onTrue(coralIntakeCommands.set(CoralIntakeConstants.State.STOP));

		primaryController.x().onTrue(coralWristCommands.set(CoralWristConstants.State.STATION));
		primaryController.y().onTrue(coralWristCommands.set(CoralWristConstants.State.L1toL3));

		tertiaryController.a().onTrue(elevatorCommands.set(ElevatorConstants.Level.L1));
		tertiaryController.b().onTrue(elevatorCommands.set(ElevatorConstants.Level.L2));
		tertiaryController.x().onTrue(elevatorCommands.set(ElevatorConstants.Level.L3));
		tertiaryController.y().onTrue(elevatorCommands.set(ElevatorConstants.Level.L4));
		tertiaryController.rightTrigger().onTrue(elevatorCommands.set(ElevatorConstants.Level.CoralStation));
	}

	/**
	 * Use this to pass the autonomous command to the main {@link Robot} class.
	 *
	 * @return the command to run in autonomous
	 */
	public Command getAutonomousCommand() {
		return autoChooser.getSelected();
	}
}
