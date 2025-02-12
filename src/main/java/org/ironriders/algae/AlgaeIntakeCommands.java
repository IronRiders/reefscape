package org.ironriders.algae;

import org.ironriders.algae.AlgaeIntakeConstants.AlgaeIntakeState;
import com.pathplanner.lib.auto.NamedCommands;

import static org.ironriders.algae.AlgaeIntakeConstants.DISCHARGE_TIMEOUT;

import edu.wpi.first.wpilibj2.command.Command;

public class AlgaeIntakeCommands {
    private final AlgaeIntakeSubsystem intake;

    public AlgaeIntakeCommands(AlgaeIntakeSubsystem intake) {
        this.intake = intake;
        NamedCommands.registerCommand("Algae Intake Grab", set(AlgaeIntakeState.GRAB));
        NamedCommands.registerCommand("Algae Intake Eject", set(AlgaeIntakeState.EJECT));
        NamedCommands.registerCommand("Algae Intake Stop", set(AlgaeIntakeState.STOP));

    }

    public Command set(AlgaeIntakeConstants.AlgaeIntakeState state) {
        Command command = intake.run(() -> intake.set(state));

        if (state.equals(AlgaeIntakeState.EJECT)) {// turns off after one second
            return command
                    .withTimeout(DISCHARGE_TIMEOUT) // Maybe in the future have this be overriden by the driver holding
                                                    // down the button
                    .finallyDo(() -> intake.set(AlgaeIntakeState.STOP));
        }

        return command.finallyDo(() -> intake.set(AlgaeIntakeState.STOP)); // turns off manipulator after command stops
    }

    public Command reset() {
        return intake.runOnce(intake::reset);
    }

    public AlgaeIntakeSubsystem getAlgaeIntake() {
        return intake;
    }
}
