package org.ironriders.coral;

import org.ironriders.coral.CoralIntakeConstants.State;
import com.pathplanner.lib.auto.NamedCommands;

import static org.ironriders.algae.AlgaeIntakeConstants.INTAKE_IMPATIENCE;
import static org.ironriders.coral.CoralIntakeConstants.DISCHARGE_TIMEOUT;

import org.ironriders.algae.AlgaeIntakeConstants.AlgaeIntakeState;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class CoralIntakeCommands {
    private final CoralIntakeSubsystem intake;

    public CoralIntakeCommands(CoralIntakeSubsystem intake) {
        this.intake = intake;
        NamedCommands.registerCommand("Coral Intake Grab", set(State.GRAB));
        NamedCommands.registerCommand("Coral Intake Eject", set(State.EJECT));
        NamedCommands.registerCommand("Coral Intake Stop", set(State.STOP));
    }

    public Command set(CoralIntakeConstants.State state) {
        Command command = intake.run(() -> intake.set(state));
        switch (state) {
            case GRAB:
                return command
                        .andThen(Commands.race(
                                Commands.runOnce(() -> {
                                    intake.getLimitSwitchTriggered();
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
}
