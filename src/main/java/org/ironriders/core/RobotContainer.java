// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;
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
import org.ironriders.lib.field.FieldElement.ElementType;
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

	private final CommandXboxController primaryController = new CommandXboxController(
			DriveConstants.PRIMARY_CONTROLLER_PORT);
	private final CommandGenericHID secondaryController = new CommandGenericHID(DriveConstants.KEYPAD_CONTROLLER_PORT);
	private final CommandXboxController tertiaryController = new CommandXboxController(
			DriveConstants.TERTIARY_CONTROLLER_PORT);

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

		primaryController.axisMagnitudeGreaterThan(
				0, DriveConstants.PATHFIND_CANCEL_THRESHOLD).onTrue(driveCommands.cancelPathfind());
		primaryController.axisMagnitudeGreaterThan(
				1, DriveConstants.PATHFIND_CANCEL_THRESHOLD).onTrue(driveCommands.cancelPathfind());

		// SECONDARY CONTROLS

		// 1/2 - Coral Home, 3/4 - Elevator Home
		secondaryController.button(1).onTrue(coralWristCommands.home());
		secondaryController.button(3).onTrue(elevatorCommands.home());

		// 5/6 - Target Station, 7/8 - Target Processor
		secondaryController.button(5).onTrue(targetingCommands.targetNearest(ElementType.STATION));
		secondaryController.button(7).onTrue(targetingCommands.targetNearest(ElementType.PROCESSOR));

		// 11/12 - Climb Up, 13/14 - Climb Reset
		secondaryController.button(11).onTrue(climbCommands.set(ClimbConstants.State.UP))
				.onFalse(climbCommands.set(ClimbConstants.State.STOP));
		secondaryController.button(15).onTrue(climbCommands.set(ClimbConstants.State.DOWN))
				.onFalse(climbCommands.set(ClimbConstants.State.STOP));

		// 9/10 - L4, 13/14 - L3 & AH, 17/18 - L2 & AL, 21/22 - L1
		secondaryController.button(9).onTrue(
			Commands.runOnce(() -> {
				GameState.setCoralTarget(ElevatorConstants.Level.L4);
			}));
		secondaryController.button(13).onTrue(
			Commands.runOnce(() -> {
				GameState.setCoralTarget(ElevatorConstants.Level.L3);
				GameState.setAlgaeTarget(ElevatorConstants.Level.L3);
			}));
		secondaryController.button(17).onTrue(
			Commands.runOnce(() -> {
				GameState.setCoralTarget(ElevatorConstants.Level.L2);
				GameState.setAlgaeTarget(ElevatorConstants.Level.L2);
			}));
		secondaryController.button(21).onTrue(
			Commands.runOnce(() -> {
				GameState.setCoralTarget(ElevatorConstants.Level.L1);
			}));

		// 23 - Coral Left, 24 - Coral Right
		secondaryController.button(23).onTrue(targetingCommands.targetReefPole(Side.Left));
		secondaryController.button(24).onTrue(targetingCommands.targetReefPole(Side.Right));

		// 19 - Eject Coral, 20 - Eject Algae
		secondaryController.button(19).onTrue(coralIntakeCommands.set(CoralIntakeConstants.State.EJECT));
		secondaryController.button(20).onTrue(algaeIntakeCommands.set(State.EJECT));

		// PRIMARY CONTROLS
		
		primaryController.rightBumper().onFalse(robotCommands.scoreAlgae());

		primaryController.leftBumper().onTrue(Commands.runOnce(() -> {
			Commands.deferredProxy(() -> {
				return robotCommands.grabAlgae(GameState.getAlgaeTarget());
			});
		}));

		primaryController.leftTrigger().onTrue(robotCommands.prepareToGrabCoral());
		primaryController.leftTrigger().onFalse(robotCommands.grabCoral());

		primaryController.a().onTrue(driveCommands.pathfindToTarget());
		primaryController.x().onTrue(driveCommands.cancelPathfind());


		primaryController.rightTrigger().onTrue(Commands.runOnce(() -> {
			robotCommands.scoreCoral(GameState.getCoralTarget()).schedule();
		}));

		primaryController.y().onTrue(driveCommands.pathfindToTarget());

		// Configure dpad as jog control. wpilib exposes dpad as goofy "pov" values
		// which are an angle; we create a trigger for each discrete 45-degree angle
		for (var angle = 0; angle < 360; angle += 45) {
			primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
		}
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
