package org.ironriders.wrist.algae;

import static org.ironriders.wrist.algae.AlgaeIntakeConstants.DISCHARGE_TIMEOUT;
import static org.ironriders.wrist.algae.AlgaeIntakeConstants.INTAKE_IMPATIENCE;

import org.ironriders.wrist.algae.AlgaeIntakeConstants.State;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class AlgaeIntakeCommands {

    private final AlgaeIntakeSubsystem intake;
    private Runnable onSuccess;

    public AlgaeIntakeCommands(AlgaeIntakeSubsystem intake) {
        this.intake = intake;

        intake.publish("Algae Intake Grab", set(State.GRAB));
        intake.publish("Algae Intake Eject", set(State.EJECT));
        intake.publish("Algae Intake Stop", set(State.STOP));
    }

    public Command set(AlgaeIntakeConstants.State state) {
        Command command = intake.run(() -> intake.set(state)).withTimeout(5);
    
        
        // Algae intake has no limit switch!  Driver must manually enable/disable
        return command;

        // switch (state) {
        //     case GRAB:
        //         return command
        //                 .andThen(Commands.race(
        //                         Commands.waitUntil(() -> {
        //                             if (intake.getLimitSwitchTriggered() && onSuccess != null) {
        //                                 onSuccess.run();
        //                             }
        //                             return intake.getLimitSwitchTriggered();
        //                         }), 
        //                         Commands.waitSeconds(INTAKE_IMPATIENCE)))
        //                 .finallyDo(() -> intake.set(State.STOP));
        //     case EJECT:
        //         return command.withTimeout(DISCHARGE_TIMEOUT).finallyDo(() -> intake.set(State.STOP));
        //     default:
        //         return command.finallyDo(() -> intake.set(State.STOP));
        // }
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
