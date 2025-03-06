package org.ironriders.wrist.coral;

import static org.ironriders.wrist.algae.AlgaeIntakeConstants.INTAKE_IMPATIENCE;
import static org.ironriders.wrist.coral.CoralIntakeConstants.DISCHARGE_TIMEOUT;

import org.ironriders.wrist.coral.CoralIntakeConstants.State;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class CoralIntakeCommands {
    private final CoralIntakeSubsystem intake;
    private Runnable onSucess;
    public CoralIntakeCommands(CoralIntakeSubsystem intake) {
        this.intake = intake;
        intake.publish("Coral Intake Grab", set(State.GRAB));
        intake.publish("Coral Intake Eject", set(State.EJECT));
        intake.publish("Coral Intake Stop", set(State.STOP));
    }

    public Command set(CoralIntakeConstants.State state) {
        Command command = intake.run(() -> intake.set(state));
        switch (state) {
            case GRAB:
                return command
                        .andThen(Commands.race(
                                Commands.waitUntil(() -> {
                                    if(intake.getLimitSwitchTriggered()&&onSucess != null){
                                        onSucess.run();
                                    }
                                    return intake.getLimitSwitchTriggered();
                                }),
                                Commands.waitSeconds(INTAKE_IMPATIENCE)))
                        .finallyDo(() -> intake.set(State.STOP));
            case EJECT:
                return command.withTimeout(DISCHARGE_TIMEOUT).finallyDo(() -> intake.set(State.STOP));
            default:
                return command.finallyDo(() -> intake.set(State.STOP));
        }
    }

    public Command reset() {
        return intake.runOnce(intake::reset);
    }

    public CoralIntakeSubsystem getCoralIntake() {
        return intake;
    }
    public void setOnSuccess(Runnable onSucess){
        this.onSucess=onSucess;
    }
}
