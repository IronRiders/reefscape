package org.ironriders.wrist.algae;

import org.ironriders.wrist.algae.AlgaeIntakeConstants.AlgaeIntakeState;

import edu.wpi.first.wpilibj2.command.Command;

public class AlgaeIntakeCommands {

    private final AlgaeIntakeSubsystem intake;
    private Runnable onSuccess;

    public AlgaeIntakeCommands(AlgaeIntakeSubsystem intake) {
        this.intake = intake;

        intake.publish("Algae Intake Grab", set(AlgaeIntakeState.GRAB));
        intake.publish("Algae Intake Eject", set(AlgaeIntakeState.EJECT));
        intake.publish("Algae Intake Stop", set(AlgaeIntakeState.STOP));
    }

    public Command set(AlgaeIntakeConstants.AlgaeIntakeState state) {
        Command command = intake.run(() -> intake.set(state)).withTimeout(5);

        // Algae intake has no limit switch! Driver must manually enable/disable
        return command;
    }

    public Command reset() {
        return intake.runOnce(intake::reset);
    }

    public AlgaeIntakeSubsystem getAlgaeIntake() {
        return intake;
    }

    public void setOnSuccess(Runnable onSucess) {
        this.onSuccess = onSucess;
    }
}
