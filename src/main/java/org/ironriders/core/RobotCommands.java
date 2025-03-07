package org.ironriders.core;

import java.util.function.DoubleSupplier;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.climb.ClimbConstants;
import org.ironriders.drive.DriveCommands;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.lib.field.FieldElement.ElementType;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.wrist.algae.AlgaeIntakeCommands;
import org.ironriders.wrist.algae.AlgaeWristCommands;
import org.ironriders.wrist.coral.CoralIntakeCommands;
import org.ironriders.wrist.coral.CoralIntakeConstants;
import org.ironriders.wrist.coral.CoralWristCommands;
import org.ironriders.wrist.coral.CoralWristConstants;
import org.ironriders.drive.DriveConstants;
import org.ironriders.wrist.algae.*;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

/**
 * These commands require more complex logic and are not directly tied to a
 * subsystem.
 * They generally interface w/ multiple subsystems via their commands and are
 * higher-level.
 * 
 * These commands are those which the driver controls call.
 */
public class RobotCommands {
	private final DriveCommands driveCommands;
	private final TargetingCommands targetingCommands;
	private final ElevatorCommands elevatorCommands;
	private final CoralWristCommands coralWristCommands;
	private final CoralIntakeCommands coralIntakeCommands;
	private final AlgaeWristCommands algaeWristCommands;
	private final AlgaeIntakeCommands algaeIntakeCommands;
	private final ClimbCommands climbCommands;
	private final GenericHID controller;

	public RobotCommands(
			DriveCommands driveCommands,
			TargetingCommands targetingCommands,
			ElevatorCommands elevatorCommands,
			CoralWristCommands coralWristCommands, CoralIntakeCommands coralIntakeCommands,
			AlgaeWristCommands algaeWristCommands, AlgaeIntakeCommands algaeIntakeCommands,
			ClimbCommands climbCommands,
			GenericHID controller) {

		this.driveCommands = driveCommands;
		this.targetingCommands = targetingCommands;
		this.elevatorCommands = elevatorCommands;
		this.coralWristCommands = coralWristCommands;
		this.coralIntakeCommands = coralIntakeCommands;
		this.algaeWristCommands = algaeWristCommands;
		this.algaeIntakeCommands = algaeIntakeCommands;
		this.climbCommands = climbCommands;
		this.controller = controller;

		// register named commands
		// NamedCommands.registerCommand("Prepare to Score Algae",
		// this.prepareToScoreAlgae());
		// NamedCommands.registerCommand("Score Algae", this.scoreAlgae());

		NamedCommands.registerCommand("Score Coral L1",
				this.scoreCoral(ElevatorConstants.Level.L1));
		NamedCommands.registerCommand("Score Coral L2",
				this.scoreCoral(ElevatorConstants.Level.L2));
		NamedCommands.registerCommand("Score Coral L3",
				this.scoreCoral(ElevatorConstants.Level.L3));
		NamedCommands.registerCommand("Score Coral L4",
				this.scoreCoral(ElevatorConstants.Level.L4));

		NamedCommands.registerCommand("Climber Down", climbCommands.set(ClimbConstants.State.DOWN));
		NamedCommands.registerCommand("Climber Up", climbCommands.set(ClimbConstants.State.UP));

		// NamedCommands.registerCommand("Prepare to Grab Algae",
		// this.prepareToGrabAlgae());
		// NamedCommands.registerCommand("Grab Algae", this.grabAlgae());

		// NamedCommands.registerCommand("Prepare to Grab Coral",
		// this.prepareToGrabCoral());
		// NamedCommands.registerCommand("Grab Coral", this.grabCoral());
	}

	/**
	 * Initialize all subsystems when first enabled.
	 * 
	 * This primarily involves homing. We need to home sequentially coral -> algae
	 * -> elevator due to physical
	 * limitations.
	 */
	public Command startup() {
		coralIntakeCommands.setOnSuccess(() -> rumble());
		algaeIntakeCommands.setOnSuccess(() -> rumble());
		return coralWristCommands.home()
				.andThen(algaeWristCommands.home())
				
				// The algae wrist should not be extended when we don't need it
				//.andThen(algaeWristCommands.set(AlgaeWristConstants.State.EXTENDED))
				
				.andThen(elevatorCommands.home());
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
		return driveCommands.driveTeleop(inputTranslationX, inputTranslationY, inputRotation, true);
	}

	/**
	 * Produce command to jog the robot at specified robot-relative angle.
	 * 
	 * Jog distance is {@value DriveConstants#JOG_DISTANCE_INCHES}.
	 */
	public Command jog(double robotRelativeAngleDegrees) {
		return driveCommands.jog(robotRelativeAngleDegrees);
	}

	public Command rumble() {
		return Commands.sequence(
				Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 1)),
				Commands.waitSeconds(0.3),
				Commands.runOnce(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0)))
				.handleInterrupt(() -> controller.setRumble(GenericHID.RumbleType.kBothRumble, 0));
	}

	public Command toggleClimber() {
		return Commands.none();
		// TODO
	}

	public Command scoreCoral(ElevatorConstants.Level level) {
		return Commands.sequence(
				elevatorCommands.set(level),
				coralWristCommands.set(switch (level) {
					case L1, L2, L3 -> CoralWristConstants.State.L1toL3;
					case L4 -> CoralWristConstants.State.L4;
					default -> {
						throw new IllegalArgumentException(
								"Cannot score coral to level: " + level);
					}
				}),
				coralIntakeCommands.set(CoralIntakeConstants.State.EJECT),
				Commands.parallel(
						coralWristCommands.set(CoralWristConstants.State.STOWED),
						elevatorCommands.set(ElevatorConstants.Level.Down)));
	}

	public Command prepareToGrabCoral() {
		return Commands.parallel(
				coralWristCommands.set(CoralWristConstants.State.STATION),
				elevatorCommands.set(ElevatorConstants.Level.CoralStation));
	}

	public Command grabCoral() {
		return Commands.sequence(
				coralIntakeCommands.set(CoralIntakeConstants.State.GRAB),
				coralWristCommands.set(CoralWristConstants.State.STOWED),
				this.rumble(),
				elevatorCommands.set(ElevatorConstants.Level.Down));
	}

	public Command scoreAlgae() {
		// TODO: option to grab coral in parallel
		return Commands.sequence(
				Commands.parallel(
						elevatorCommands.set(ElevatorConstants.Level.Down),
						algaeWristCommands.set(AlgaeWristConstants.State.EXTENDED)),
				algaeIntakeCommands.set(AlgaeIntakeConstants.State.EJECT),
				algaeIntakeCommands.set(AlgaeIntakeConstants.State.STOP),
				algaeWristCommands.set(AlgaeWristConstants.State.STOWED));
	}

	public Command grabAlgae(ElevatorConstants.Level level) {
		return Commands.sequence(
				Commands.parallel(
						elevatorCommands.set(level),
						algaeWristCommands.set(AlgaeWristConstants.State.EXTENDED),
						algaeIntakeCommands.set(AlgaeIntakeConstants.State.GRAB)),
				algaeIntakeCommands.set(AlgaeIntakeConstants.State.GRAB),
				algaeWristCommands.set(AlgaeWristConstants.State.STOWED),
				this.rumble());
	}
}
