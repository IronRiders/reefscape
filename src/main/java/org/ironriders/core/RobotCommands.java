package org.ironriders.Core;

import java.util.function.DoubleSupplier;
import org.ironriders.Manipulators.Algae.AlgaeIntake.AlgaeIntakeSubsystem;
import org.ironriders.Manipulators.Algae.AlgaeIntake.AlgaeIntakeCommands;
import org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristCommands;
import org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants;
import org.ironriders.Manipulators.Coral.CoralIntake.CoralIntakeCommands;
import org.ironriders.Manipulators.Coral.CoralIntake.CoralIntakeConstants;
import org.ironriders.Manipulators.Coral.CoralWrist.CoralWristCommands;
import org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants;
import org.ironriders.Manipulators.Elevator.ElevatorCommands;
import org.ironriders.Manipulators.Elevator.ElevatorConstants;
import org.ironriders.Drive.DriveCommands;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.climb.ClimbCommands;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

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
    private final CommandJoystick secondaryController;
    private final GenericHID controller1;

    public RobotCommands(
        DriveCommands driveCommands,
        TargetingCommands targetingCommands,
        ElevatorCommands elevatorCommands,
        CoralWristCommands coralWristCommands,  
        CoralIntakeCommands coralIntakeCommands,
        AlgaeWristCommands algaeWristCommands,
        AlgaeIntakeCommands algaeIntakeCommands,
        ClimbCommands climbCommands,
        GenericHID controller1,
        CommandJoystick secondaryController) {

    this.driveCommands = driveCommands;
    this.targetingCommands = targetingCommands;
    this.elevatorCommands = elevatorCommands;
    this.coralWristCommands = coralWristCommands; 
    this.coralIntakeCommands = coralIntakeCommands;
    this.algaeWristCommands = algaeWristCommands;
    this.algaeIntakeCommands = algaeIntakeCommands;
    this.climbCommands = climbCommands;
    this.controller1 = controller1;
    this.secondaryController = secondaryController;
}


    public Command startup() {
        coralIntakeCommands.setOnSuccess(this::rumble);
        //algaeIntakeCommands.setOnSuccess(this::rumble);
        return coralWristCommands.home()
                .andThen(algaeWristCommands.home())
                .andThen(algaeWristCommands.set(AlgaeWristConstants.AlgaeWristState.EXTENDED))
                .andThen(elevatorCommands.home());
    }

    public Command driveTeleop(DoubleSupplier inputTranslationX, DoubleSupplier inputTranslationY,
            DoubleSupplier inputRotation) {
        return driveCommands.driveTeleop(inputTranslationX, inputTranslationY, inputRotation, true);
    }

    public Command jog(double robotRelativeAngleDegrees) {
        return driveCommands.jog(robotRelativeAngleDegrees);
    }

    public Command rumble() {
        return Commands.sequence(
                Commands.runOnce(() -> controller1.setRumble(GenericHID.RumbleType.kBothRumble, 1)),
                Commands.waitSeconds(0.3),
                Commands.runOnce(() -> controller1.setRumble(GenericHID.RumbleType.kBothRumble, 0)))
                .handleInterrupt(() -> controller1.setRumble(GenericHID.RumbleType.kBothRumble, 0));
    }

    public Command toggleClimber() {
        return Commands.none();
    }

    public Command moveElevatorAndWrist(ElevatorConstants.Level level) {
        return Commands.sequence(
                elevatorCommands.set(level),
                Commands.parallel(
                        algaeWristCommands.set(AlgaeWristConstants.AlgaeWristState.STOWED),
                        coralWristCommands.set(switch (level) {
                            case L1, L2, L3 -> CoralWristConstants.CoralWristState.L1toL3;
                            case L4 -> CoralWristConstants.CoralWristState.L4;
                            case CoralStation -> CoralWristConstants.CoralWristState.STATION;
                            case Down -> CoralWristConstants.CoralWristState.STOWED;
                            default -> throw new IllegalArgumentException(
                                    "Cannot score coral to level: " + level);
                        })));
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

	public Command runAlgaeIntakeWithJoystick() {
		return algaeIntakeCommands.runIntakeWithJoystick();
	}
	
}
