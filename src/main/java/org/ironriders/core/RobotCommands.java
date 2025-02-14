package org.ironriders.core;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import org.ironriders.algae.AlgaeIntakeConstants;
import org.ironriders.algae.AlgaeWristConstants;
import org.ironriders.coral.CoralIntakeConstants;
import org.ironriders.coral.CoralWristConstants;
import org.ironriders.algae.AlgaeIntakeCommands;
import org.ironriders.algae.AlgaeIntakeSubsystem;
import org.ironriders.algae.AlgaeWristCommands;
import org.ironriders.algae.AlgaeWristSubsystem;
import org.ironriders.algae.AlgaeWristConstants.AlgaeWristState;
import org.ironriders.algae.AlgaeIntakeConstants.AlgaeIntakeState;
import org.ironriders.coral.CoralIntakeCommands;
import org.ironriders.coral.CoralIntakeSubsystem;
import org.ironriders.coral.CoralWristCommands;
import org.ironriders.coral.CoralWristSubsystem;
import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.drive.DriveConstants;
import org.ironriders.vision.VisionCommands;
import org.ironriders.vision.VisionSubsystem;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import java.util.function.Supplier;

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
    private final ElevatorCommands elevatorCommands;
    // private final CoralWristCommands coralWristCommands;
    // private final CoralIntakeCommands coralIntakeCommands;
    // private final AlgaeWristCommands algaeWristCommands;
    // private final AlgaeIntakeCommands algaeIntakeCommands;
    private final VisionCommands visionCommands;
    private final GenericHID controller;

    public RobotCommands(
            DriveCommands driveCommands, 
            ElevatorCommands elevatorCommands,
            // CoralWristCommands coralWristCommands, CoralIntakeCommands coralIntakeCommands, 
            // AlgaeWristCommands algaeWristCommands, AlgaeIntakeCommands algaeIntakeCommands, 
            VisionCommands visionCommands, GenericHID controller
    ) {
        this.driveCommands = driveCommands;
        this.elevatorCommands = elevatorCommands;
        // this.coralWristCommands = coralWristCommands;
        // this.coralIntakeCommands = coralIntakeCommands;
        // this.algaeWristCommands = algaeWristCommands;
        // this.algaeIntakeCommands = algaeIntakeCommands;
        this.visionCommands = visionCommands;
        this.controller = controller;
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
        if (DriverStation.isAutonomous())
            return Commands.none();

        return driveCommands.drive(
                () -> new Translation2d(
                        inputTranslationX.getAsDouble() * DriveConstants.SWERVE_MAXIMUM_SPEED_TELEOP,
                        inputTranslationY.getAsDouble() * DriveConstants.SWERVE_MAXIMUM_SPEED_TELEOP),
                () -> inputRotation.getAsDouble() * DriveConstants.SWERVE_MAXIMUM_SPEED_TELEOP,
                () -> true);
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

    // public Command prepareToScoreAlgae() {
    //     return Commands.parallel(
    //             elevatorCommands.setLevel(ElevatorConstants.Level.L1),
    //             algaeWristCommands.set(AlgaeWristState.EXTENDED));
    // }

    // public Command scoreAlgae() {
    //     return Commands.sequence(
    //             algaeIntakeCommands.set(AlgaeIntakeState.EJECT));
    // }

    // public Command prepareToScoreCoral(ElevatorConstants.Level level) {
    //     return Commands.parallel(
    //             elevatorCommands.setLevel(level),
    //             coralWristCommands.set(level != ElevatorConstants.Level.L4 ? CoralWristConstants.State.L1toL3
    //                     : CoralWristConstants.State.L4));
    // }

    // public Command scoreCoral() {
    //     return Commands.sequence(
    //             coralIntakeCommands.set(CoralIntakeConstants.State.EJECT),
    //             elevatorCommands.setLevel(ElevatorConstants.Level.L1));
    // }

    public Command grabAlgae() {
        return Commands.none();
        // TODO: Implement option(?) for scoring algae at the same time, and option for
        // golfball
    }

    public Command grabCoral() {
        return Commands.none();
        // TODO
    }
}
