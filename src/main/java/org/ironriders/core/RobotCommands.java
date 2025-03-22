package org.ironriders.core;

import java.util.function.DoubleSupplier;

import org.ironriders.climb.ClimbCommands;
import org.ironriders.drive.DriveCommands;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.wrist.algae.AlgaeIntakeCommands;
import org.ironriders.wrist.algae.AlgaeIntakeConstants;
import org.ironriders.wrist.algae.AlgaeWristCommands;
import org.ironriders.wrist.algae.AlgaeWristConstants;
import org.ironriders.wrist.algae.AlgaeWristConstants.AlgaeWristState;
import org.ironriders.wrist.coral.CoralIntakeCommands;
import org.ironriders.wrist.coral.CoralIntakeConstants;
import org.ironriders.wrist.coral.CoralWristCommands;
import org.ironriders.wrist.coral.CoralWristConstants;

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
@SuppressWarnings("unused")
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

		// TODO: Named commands, implement along w/ on-the-fly autos
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
				.andThen(climbCommands.rezero())
				.andThen(algaeWristCommands.home())
				//Not commenting the line below breaks elvator????? 
				//This is new???
				//.andThen(algaeWristCommands.set(AlgaeWristConstants.AlgaeWristState.EXTENDED))
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
		//return climbCommands.goTo(Targets.TARGET);
	}

	public Command moveElevatorAndWrist(ElevatorConstants.Level level) {
		return Commands.sequence(
				elevatorCommands.set(level),
				// Commands.parallel(
				// algaeWristCommands.set(AlgaeWristState.STOWED),
				coralWristCommands.set(switch (level) {
					case L1, L2, L3 -> CoralWristConstants.CoralWristState.L1toL3;
					case L4 -> CoralWristConstants.CoralWristState.L4;
					case CoralStation -> CoralWristConstants.CoralWristState.STATION;
					case Down -> CoralWristConstants.CoralWristState.STOWED;
					case HighAlgae -> CoralWristConstants.CoralWristState.L1toL3;
					default -> {
						throw new IllegalArgumentException(
								"Cannot score coral to level: " + level);
					}
				}));
	}

	public Command scoreCoral() {
		return Commands.sequence(
				coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.EJECT),
				Commands.parallel(
						coralWristCommands.set(CoralWristConstants.CoralWristState.STOWED),
						elevatorCommands.set(ElevatorConstants.Level.Down)));
	}

	public Command prepareToGrabCoral() {
		return Commands.parallel(
				coralWristCommands.set(CoralWristConstants.CoralWristState.STATION),
				elevatorCommands.set(ElevatorConstants.Level.CoralStation));
	}

	public Command grabCoral() {
		return Commands.sequence(
				coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.GRAB),
				coralWristCommands.set(CoralWristConstants.CoralWristState.STOWED),
				elevatorCommands.set(ElevatorConstants.Level.Down));
	}

	public Command scoreAlgae() {
		// TODO: option to grab coral in parallel
		return Commands.sequence(
				Commands.parallel(
						elevatorCommands.set(ElevatorConstants.Level.Down),
						algaeWristCommands.set(AlgaeWristConstants.AlgaeWristState.EXTENDED)),
				algaeIntakeCommands.set(AlgaeIntakeConstants.AlgaeIntakeState.EJECT),
				algaeIntakeCommands.set(AlgaeIntakeConstants.AlgaeIntakeState.STOP));
	}

	public Command grabAlgae(ElevatorConstants.Level level) {
		return Commands.sequence(
				Commands.parallel(
						elevatorCommands.set(level),
						algaeWristCommands.set(AlgaeWristConstants.AlgaeWristState.EXTENDED),
						algaeIntakeCommands.set(AlgaeIntakeConstants.AlgaeIntakeState.GRAB)),
				algaeIntakeCommands.set(AlgaeIntakeConstants.AlgaeIntakeState.GRAB));
	}
}
