package org.ironriders.manipulators;

import org.ironriders.manipulators.IntakeConstants.State;
import com.pathplanner.lib.auto.NamedCommands;

import static org.ironriders.manipulators.IntakeConstants.DISCHARGE_TIMEOUT;

import edu.wpi.first.wpilibj2.command.Command;


public class AlgaeIntakeCommands {
    private final AlgaeIntakeSubsystem intake;
    
    public AlgaeIntakeCommands(AlgaeIntakeSubsystem intake){
        this.intake =intake;
        NamedCommands.registerCommand("Algae Intake Grab", set(State.GRAB));
        NamedCommands.registerCommand("Algae Intake Eject", set(State.EJECT));
        NamedCommands.registerCommand("Algae Intake Stop", set(State.STOP));

    }

    public Command set(IntakeConstants.State state){
        Command command = intake.run(() -> intake.set(state));

        if (state.equals(State.EJECT) ) {// turns off after one second
            return command
                    .withTimeout(DISCHARGE_TIMEOUT) // Maybe in the future have this be overriden by the driver holding down the button 
                    .finallyDo(() -> intake.set(State.STOP));
        }

        return command.finallyDo(() -> intake.set(State.STOP)); // turns off manipulator after command stops
    }

    

    public Command reset(){
        return intake.runOnce(intake::reset);
    }

    public AlgaeIntakeSubsystem getAlgaeIntake(){
        return intake;
    }
}
