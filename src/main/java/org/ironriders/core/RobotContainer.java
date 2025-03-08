package org.ironriders.core;

import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.wrist.algae.AlgaeIntakeCommands;
import org.ironriders.wrist.algae.AlgaeIntakeSubsystem;
import org.ironriders.wrist.algae.AlgaeWristCommands;
import org.ironriders.wrist.algae.AlgaeWristSubsystem;
import org.ironriders.climb.ClimbCommands;
import org.ironriders.climb.ClimbSubsystem;
import org.ironriders.climb.ClimbConstants;
import org.ironriders.wrist.coral.CoralIntakeCommands;
import org.ironriders.wrist.coral.CoralIntakeSubsystem;
import org.ironriders.wrist.coral.CoralWristCommands;
import org.ironriders.wrist.coral.CoralWristSubsystem;
import org.ironriders.dash.DashboardSubsystem;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorSubsystem;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.lib.GameState;
import org.ironriders.lib.RobotUtils;
import org.ironriders.lib.field.FieldElement.ElementType;
import org.ironriders.lib.field.FieldPose.Side;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.targeting.TargetingSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import com.pathplanner.lib.auto.AutoBuilder;

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
	 * Use this method to define your trigger->command mappings.
	 */
	private void configureBindings() {

		// DRIVE CONTROLS (Primary Controller)
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

		primaryController.x().onTrue(driveCommands.cancelPathfind());
		primaryController.y().onTrue(driveCommands.pathfindToTarget());

		// SECONDARY CONTROLLER (Operator)
		secondaryController.button(ControllerConstants.coralWristHome).onTrue(coralWristCommands.home());
		secondaryController.button(ControllerConstants.elevatorHome).onTrue(elevatorCommands.home());

		secondaryController.button(ControllerConstants.targetStation).onTrue(targetingCommands.targetNearest(ElementType.STATION));
		secondaryController.button(ControllerConstants.targetProcessor).onTrue(targetingCommands.targetNearest(ElementType.PROCESSOR));

		secondaryController.button(ControllerConstants.climbUp).onTrue(climbCommands.set(ClimbConstants.State.UP))
				.onFalse(climbCommands.set(ClimbConstants.State.STOP));
		secondaryController.button(ControllerConstants.climbDown).onTrue(climbCommands.set(ClimbConstants.State.DOWN))
				.onFalse(climbCommands.set(ClimbConstants.State.STOP));

		// Elevator Levels
		secondaryController.button(ControllerConstants.setL4).onTrue(Commands.runOnce(() -> {
			GameState.setCoralTarget(ElevatorConstants.Level.L4);
		}));
		secondaryController.button(ControllerConstants.setL3).onTrue(Commands.runOnce(() -> {
			GameState.setCoralTarget(ElevatorConstants.Level.L3);
		}));
		secondaryController.button(ControllerConstants.setL2).onTrue(Commands.runOnce(() -> {
			GameState.setCoralTarget(ElevatorConstants.Level.L2);
		}));
		secondaryController.button(ControllerConstants.setL1).onTrue(Commands.runOnce(() -> {
			GameState.setCoralTarget(ElevatorConstants.Level.L1);
		}));

		// Targeting Reef Poles (X-Axis on Secondary Controller)
		secondaryController.axisGreaterThan(ControllerConstants.targetReefAxis, 0.5)
			.onTrue(targetingCommands.targetReefPole(Side.Right));

		secondaryController.axisLessThan(ControllerConstants.targetReefAxis, -0.5)
			.onTrue(targetingCommands.targetReefPole(Side.Left));

		// **Moving Intaking & Scoring to Secondary Controller**
		
		secondaryController.button(ControllerConstants.scoreCoral).onTrue(robotCommands.scoreCoral(GameState.getCoralTarget()));

		// **Moving Intaking & Scoring to Secondary Controller (Using Y-Axis)**
		secondaryController.axisLessThan(ControllerConstants.controlAlgaeAxis, -0.5).onTrue(robotCommands.scoreAlgae());

		secondaryController.axisGreaterThan(ControllerConstants.controlAlgaeAxis, 0.5).onTrue(Commands.runOnce(() -> {
			Commands.deferredProxy(() -> {
				return robotCommands.grabAlgae(GameState.getAlgaeTarget());
			});
		}));


		secondaryController.axisGreaterThan(ControllerConstants.prepareGrabCoralTrigger, 0.5)
    	.onTrue(robotCommands.prepareToGrabCoral());

		secondaryController.axisLessThan(ControllerConstants.prepareGrabCoralTrigger, -0.5)
    	.onTrue(robotCommands.grabCoral()); // Runs when trigger is released

	
	}

	/**
	 * Use this to pass the autonomous command to the main {@link Robot} class.
	 */
	public Command getAutonomousCommand() {
		return autoChooser.getSelected();
	}
}