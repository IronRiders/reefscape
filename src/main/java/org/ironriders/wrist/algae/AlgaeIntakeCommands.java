package org.ironriders.wrist.algae;

import static org.ironriders.wrist.algae.AlgaeIntakeConstants.DISCHARGE_TIMEOUT;
import static org.ironriders.wrist.algae.AlgaeIntakeConstants.INTAKE_IMPATIENCE;

import org.ironriders.wrist.algae.AlgaeIntakeConstants.AlgaeIntakeState;
import org.ironriders.wrist.coral.CoralIntakeConstants.State;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

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
        Command command = intake.run(() -> intake.set(state));
        switch (state) {
            case GRAB:
                return command
                        .andThen(Commands.race(
                                Commands.waitUntil(() -> {
                                    if (intake.getLimitSwitchTriggered() && onSuccess != null) {
                                        onSuccess.run();
                                    }
                                    return intake.getLimitSwitchTriggered();
                                }),
                                Commands.waitSeconds(INTAKE_IMPATIENCE)))
                        .finallyDo(() -> intake.set(AlgaeIntakeState.STOP));
            case EJECT:
                return command.withTimeout(DISCHARGE_TIMEOUT).finallyDo(() -> intake.set(AlgaeIntakeState.STOP));
            default:
                return command.finallyDo(() -> intake.set(AlgaeIntakeState.STOP));
        }
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
