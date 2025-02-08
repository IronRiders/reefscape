package org.ironriders.core;

import java.util.function.DoubleSupplier;

import org.ironriders.algae.AlgaeIntakeCommands;
import org.ironriders.algae.AlgaeIntakeSubsystem;
import org.ironriders.algae.AlgaeWristCommands;
import org.ironriders.algae.AlgaeWristSubsystem;
import org.ironriders.coral.CoralIntakeCommands;
import org.ironriders.coral.CoralIntakeSubsystem;
import org.ironriders.coral.CoralWristCommands;
import org.ironriders.coral.CoralWristSubsystem;
import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.drive.DriveConstants;
import org.ironriders.vision.VisionCommands;
import org.ironriders.vision.VisionSubsystem;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
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
    private final CoralWristCommands coralWristCommands;
    private final CoralIntakeCommands coralIntakeCommands;
    private final AlgaeWristCommands algaeWristCommands;
    private final AlgaeIntakeCommands algaeIntakeCommands;
    private final VisionCommands visionCommands;

    public RobotCommands(DriveCommands driveCommands, CoralWristCommands coralWristCommands,
            CoralIntakeCommands coralIntakeCommands, AlgaeWristCommands algaeWristCommands,
            AlgaeIntakeCommands algaeIntakeCommands, VisionCommands visionCommands) {
        this.driveCommands = driveCommands;
        this.coralWristCommands = coralWristCommands;
        this.coralIntakeCommands = coralIntakeCommands;
        this.algaeWristCommands = algaeWristCommands;
        this.algaeIntakeCommands = algaeIntakeCommands;
        this.visionCommands = visionCommands;
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
                        inputTranslationX.getAsDouble() * DriveConstants.SWERVE_MAXIMUM_SPEED,
                        inputTranslationY.getAsDouble() * DriveConstants.SWERVE_MAXIMUM_SPEED),
                () -> inputRotation.getAsDouble() * DriveConstants.SWERVE_MAXIMUM_SPEED,
                () -> true
        );
    }
}
