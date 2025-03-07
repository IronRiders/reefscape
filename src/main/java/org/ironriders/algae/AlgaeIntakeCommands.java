package org.ironriders.algae;

import org.ironriders.algae.AlgaeIntakeConstants.AlgaeIntakeState;
import static org.ironriders.algae.AlgaeIntakeConstants.DISCHARGE_TIMEOUT;
import static org.ironriders.algae.AlgaeIntakeConstants.INTAKE_IMPATIENCE;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class AlgaeIntakeCommands {
    private final AlgaeIntakeSubsystem intake;

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
                                Commands.runOnce(() -> {
                                    intake.getLimitSwitchTriggered();
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
}
