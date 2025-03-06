// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;

import java.lang.ModuleLayer.Controller;
import java.lang.annotation.ElementType;

import org.ironriders.wrist.algae.AlgaeIntakeCommands;
import org.ironriders.wrist.algae.AlgaeIntakeConstants;
import org.ironriders.wrist.algae.AlgaeIntakeSubsystem;
import org.ironriders.wrist.algae.AlgaeWristCommands;
import org.ironriders.wrist.algae.AlgaeWristConstants;
import org.ironriders.wrist.algae.AlgaeWristSubsystem;
import org.ironriders.wrist.algae.AlgaeIntakeConstants.State;
import org.ironriders.climb.ClimbCommands;
import org.ironriders.climb.ClimbSubsystem;
import org.ironriders.climb.ClimbConstants;
import org.ironriders.wrist.coral.CoralIntakeCommands;
import org.ironriders.wrist.coral.CoralIntakeConstants;
import org.ironriders.wrist.coral.CoralIntakeSubsystem;
import org.ironriders.wrist.coral.CoralWristCommands;
import org.ironriders.wrist.coral.CoralWristConstants;
import org.ironriders.wrist.coral.CoralWristSubsystem;
import org.ironriders.dash.DashboardSubsystem;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorSubsystem;
import org.ironriders.elevator.ElevatorConstants.Level;
import org.ironriders.lib.GameState;
import org.ironriders.lib.RobotUtils;
import org.ironriders.lib.field.FieldPose.Side;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.targeting.TargetingSubsystem;
import org.ironriders.elevator.ElevatorConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.ironriders.wrist.coral.CoralIntakeConstants;
import org.ironriders.vision.Vision;
import org.photonvision.PhotonCamera;

import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
//import edu.wpi.*;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;

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

	private final AlgaeIntakeSubsystem algaeIntakeSubsystem = new AlgaeIntakeSubsystem();
	private final AlgaeIntakeCommands algaeIntakeCommands = algaeIntakeSubsystem.getCommands();

	private final DashboardSubsystem dashboardSubsystem = new DashboardSubsystem();
	
	private final ClimbCommands climbCommands = new ClimbSubsystem().getCommands();

	private final SendableChooser<Command> autoChooser;

	private final CommandXboxController primaryController = new CommandXboxController(DriveConstants.PRIMARY_CONTROLLER_PORT);
	private final CommandGenericHID secondaryController = new CommandGenericHID(DriveConstants.KEYPAD_CONTROLLER_PORT);
	private final CommandXboxController tertiaryController = new CommandXboxController(DriveConstants.TERTIARY_CONTROLLER_PORT);

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

		primaryController.axisMagnitudeGreaterThan(
			0, DriveConstants.PATHFIND_CANCEL_THRESHOLD).onTrue(driveCommands.cancelPathfind());
		primaryController.axisMagnitudeGreaterThan(
			1, DriveConstants.PATHFIND_CANCEL_THRESHOLD).onTrue(driveCommands.cancelPathfind());

		// secondary controls

		// 1 & 2 - coral home
		secondaryController.button(1).onTrue(coralWristCommands.home());
		// 3 & 4 - elevator home
		secondaryController.button(3).onTrue(elevatorCommands.home());
		// 5 & 6 - intake
		secondaryController.button(5).onTrue(coralIntakeCommands.set(CoralIntakeConstants.State.GRAB).andThen(algaeIntakeCommands.set(AlgaeIntakeConstants.State.GRAB))).onFalse(coralIntakeCommands.set(CoralIntakeConstants.State.STOP).andThen(algaeIntakeCommands.set(AlgaeIntakeConstants.State.STOP)));
		// 7 & 8 - processor
		secondaryController.button(7).onTrue(targetingCommands.targetNearest(org.ironriders.lib.field.FieldElement.ElementType.PROCESSOR).andThen(driveCommands.pathfindToTarget()));
		// 9 & 10 - L4
		secondaryController.button(9).onTrue(robotCommands.scoreCoral(Level.L4));
		// 11 & 12 - Climb up
		secondaryController.button(11).onTrue(climbCommands.set(ClimbConstants.State.UP)).onFalse(climbCommands.set(ClimbConstants.State.STOP));
		// 13 - L3
		secondaryController.button(13).onTrue(robotCommands.scoreCoral(Level.L3));
		// 14 - algae 2
		secondaryController.button(14).onTrue(robotCommands.prepareToGrabAlgae(Level.L4)).onFalse(robotCommands.grabAlgae());
		// 15 & 16 - climber rst
		secondaryController.button(15).onTrue(climbCommands.set(ClimbConstants.State.DOWN)).onFalse(climbCommands.set(ClimbConstants.State.STOP));
		// 17 - coral L2
		secondaryController.button(17).onTrue(robotCommands.scoreCoral(Level.L2));
		// 18 - algae 1
		secondaryController.button(18).onTrue(robotCommands.prepareToGrabAlgae(Level.L3)).onFalse(robotCommands.grabAlgae());
		// 19 - eject coral
		secondaryController.button(19).onTrue(coralIntakeCommands.set(org.ironriders.wrist.coral.CoralIntakeConstants.State.EJECT));
		// 20 - eject algae
		secondaryController.button(20).onTrue(algaeIntakeCommands.set(State.EJECT));
		// 21 & 22 - L1
		secondaryController.button(21).onTrue(robotCommands.scoreCoral(Level.L1));
		// 23 - l coral
		secondaryController.button(23).onTrue(targetingCommands.targetReefPole(Side.Left));
		// 24 - r coral
		secondaryController.button(24).onTrue(targetingCommands.targetReefPole(Side.Right));

		// various scoring controls and such (bumper for coral, trigger for algae, rightside for score, lefside for grab)
		primaryController.rightBumper().onTrue(robotCommands.prepareToScoreAlgae());
		primaryController.rightBumper().onFalse(robotCommands.scoreAlgae());

		primaryController.rightTrigger().onTrue(Commands.runOnce(() -> { 
			Commands.deferredProxy(() -> { return robotCommands.scoreCoral(GameState.getCoralTarget()); }); }));

		//primaryController.leftBumper().onTrue(robotCommands.prepareToGrabAlgae());
		//primaryController.leftBumper().onFalse(robotCommands.grabAlgae());

		primaryController.leftTrigger().onTrue(robotCommands.prepareToGrabCoral());
		primaryController.leftTrigger().onFalse(robotCommands.grabCoral());

		// Configure dpad as jog control.  wpilib exposes dpad as goofy "pov" values which are an angle; we create a
		// trigger for each discrete 45-degree angle
		for (var angle = 0; angle < 360; angle+= 45) {
			primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
		}

		primaryController.y().onTrue(Commands.deferredProxy(() -> { return robotCommands.scoreCoralMiniauto(GameState.getCoralTarget()); }));
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
