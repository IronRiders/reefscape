package org.ironriders.Manipulators.Coral.CoralIntake;

import static org.ironriders.Manipulators.Coral.CoralIntake.CoralIntakeConstants.DISCHARGE_TIMEOUT;
import static org.ironriders.Manipulators.Coral.CoralIntake.CoralIntakeConstants.INTAKE_IMPATIENCE;

import org.ironriders.Manipulators.Coral.CoralIntake.CoralIntakeConstants.CoralIntakeState;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class CoralIntakeCommands {

    private final CoralIntakeSubsystem intake;
    private Runnable onSuccess;

    public CoralIntakeCommands(CoralIntakeSubsystem intake) {
        this.intake = intake;

        intake.publish("Coral Intake Grab", set(CoralIntakeState.GRAB));
        intake.publish("Coral Intake Eject", set(CoralIntakeState.EJECT));
        intake.publish("Coral Intake Stop", set(CoralIntakeState.STOP));
    }

    public Command set(CoralIntakeConstants.CoralIntakeState state) {
        Command command = intake.run(() -> intake.set(state));

        switch (state) {
            case GRAB:
                // making an actual command override here, mostly for convenience
                return new Command() {
                    public void execute() {
                        intake.set(CoralIntakeState.GRAB);
                    }

                    public boolean isFinished() {
                        if (intake.getLimitSwitchTriggered()) {
                            onSuccess.run();
                        }
                        return intake.getLimitSwitchTriggered();
                    }
                }.withDeadline(Commands.waitSeconds(INTAKE_IMPATIENCE));
            case EJECT:
                return command.withTimeout(DISCHARGE_TIMEOUT).finallyDo(() -> intake.set(CoralIntakeState.STOP));
            default:
                return command.finallyDo(() -> intake.set(CoralIntakeState.STOP));
        }
    }

    public Command reset() {
        return intake.runOnce(() -> intake.set(CoralIntakeState.STOP));
    }

    public CoralIntakeSubsystem getCoralIntake() {
        return intake;
    }

    public void setOnSuccess(Runnable onSucess) {
        this.onSuccess = onSucess;
    }
}
