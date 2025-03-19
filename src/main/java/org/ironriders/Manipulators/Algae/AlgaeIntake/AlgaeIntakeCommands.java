package org.ironriders.Manipulators.Algae.AlgaeIntake;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

public class AlgaeIntakeCommands {

    private final AlgaeIntakeSubsystem intake;
    private final CommandJoystick secondaryController;

    public AlgaeIntakeCommands(AlgaeIntakeSubsystem intake, CommandJoystick secondaryController) {
        this.intake = intake;
        this.secondaryController = secondaryController;
    }

    /**
     * Runs the algae intake dynamically based on joystick input.
     * - Positive Y-axis ejects algae (scoring).
     * - Negative Y-axis intakes algae (grabbing).
     */
    public Command runIntakeWithJoystick() {
        return Commands.run(() -> intake.setFromJoystick(), intake);
    }

    /**
     * Stops the algae intake motor when released.
     */
    public Command stopIntake() {
        return Commands.runOnce(() -> intake.set(0.0), intake);
    }

    /**
     * Resets the intake state.
     */
    public Command reset() {
        return intake.runOnce(intake::reset);
    }

    /**
     * @return The algae intake subsystem.
     */
    public AlgaeIntakeSubsystem getIntakeSubsystem() {
        return intake;
    }
}
