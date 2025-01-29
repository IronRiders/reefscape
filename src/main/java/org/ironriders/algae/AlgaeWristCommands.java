package org.ironriders.algae;


import org.ironriders.coral.CoralWristConstants.*;
import edu.wpi.first.wpilibj2.command.Command;
import com.pathplanner.lib.auto.NamedCommands;

public class AlgaeWristCommands {
    private final AlgaeWristSubsystem algaeWrist;

    public AlgaeWristCommands(AlgaeWristSubsystem wrist) {
        this.algaeWrist = wrist;

        NamedCommands.registerCommand("Wrist Coral Station", set(State.STATION));
        NamedCommands.registerCommand("Wrist Upright", set(State.STOWED));
        NamedCommands.registerCommand("Wrist L1-L3", set(State.L1toL3));
        NamedCommands.registerCommand("Wrist L4", set(State.L4));
    }

    public Command set(State state) {
        return algaeWrist
                .runOnce(() -> algaeWrist.set(state.getPostion()))
                .until(algaeWrist::atPosition)
                .handleInterrupt(algaeWrist::reset);
    }

    public Command reset() {
        return algaeWrist.runOnce(algaeWrist::reset);
    }

    public AlgaeWristSubsystem getAlgaeWrist() {
        return algaeWrist;
    }
}
