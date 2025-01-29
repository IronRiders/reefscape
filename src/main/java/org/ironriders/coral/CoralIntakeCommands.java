package org.ironriders.coral;

import org.ironriders.coral.CoralIntakeConstants.State;
import com.pathplanner.lib.auto.NamedCommands;

import static org.ironriders.coral.CoralIntakeConstants.DISCHARGE_TIMEOUT;
import edu.wpi.first.wpilibj2.command.Command;


public class CoralIntakeCommands {
    private final CoralIntakeSubsystem intake;
    
    public CoralIntakeCommands(CoralIntakeSubsystem intake){
        this.intake =intake;
        NamedCommands.registerCommand("Coral Intake Grab", set(State.GRAB));
        NamedCommands.registerCommand("Coral Intake Eject", set(State.EJECT));
        NamedCommands.registerCommand("Coral Intake Stop", set(State.STOP));

    }

    public Command set(CoralIntakeConstants.State state){
        Command command = intake.run(() -> intake.set(state));

        if (state.equals(State.EJECT) ) {
            return command
                    .withTimeout(DISCHARGE_TIMEOUT) // Maybe in the future have this be overriden by the driver holding down the button 
                    .finallyDo(() -> intake.set(State.STOP));
        }

        return command.finallyDo(() -> intake.set(State.STOP));
    }

    

    public Command reset(){
        return intake.runOnce(intake::reset);
    }

    public CoralIntakeSubsystem getCoralIntake(){
        return intake;
    }
}
