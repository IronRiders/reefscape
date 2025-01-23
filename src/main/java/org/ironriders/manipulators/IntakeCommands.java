package org.ironriders.manipulators;

import org.ironriders.manipulators.IntakeConstants.State;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj2.command.Command;


public class IntakeCommands {
    private final IntakeSubsystem intake;

    public IntakeCommands(IntakeSubsystem intake){
        this.intake =intake;
        NamedCommands.registerCommand("Algae Intake Grab", set(State.GRAB));
       

    }

    public Command set(IntakeConstants.State state){
        Command command = intake.run(() -> intake.set(state));

        if (state.equals(State.EJECT) ) {
            return command
                    .withTimeout(DISCHARGE_TIMEOUT)
                    .finallyDo(() -> manipulator.set(STOP));
        }

        return command.finallyDo(() -> manipulator.set(STOP));
    }

    

    public Command reset(){
        return intake.runOnce(intake::reset);
    }

}
