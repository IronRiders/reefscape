package org.ironriders.manipulators;


import org.ironriders.manipulators.ManipulatorConstants.State;
import edu.wpi.first.wpilibj2.command.Command;
import com.pathplanner.lib.auto.NamedCommands;

public class CoralWristCommands {
    private final CoralWristSubsystem wrist;

    public CoralWristCommands(CoralWristSubsystem wrist){
        this.wrist = wrist;

        NamedCommands.registerCommand("Wrist Coral Station", set(State.STATION));
        NamedCommands.registerCommand("Wrist Upright", set(State.STOWED));
        NamedCommands.registerCommand("Wrist L1-L3", set(State.L1toL3));
        NamedCommands.registerCommand("Wrist L4", set(State.L4));
    }

    public Command set(State state){
        return wrist
            .runOnce(() -> wrist.set(state.getPostion()))
            .until(wrist::atPosition)
            .handleInterrupt(wrist::reset);
    }

    public Command reset(){
        return wrist.runOnce(wrist::reset);
    }

    public CoralWristSubsystem getCoralWrist(){
        return wrist;
    }


}
