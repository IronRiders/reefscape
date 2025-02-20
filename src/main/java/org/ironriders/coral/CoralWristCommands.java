package org.ironriders.coral;

import org.ironriders.coral.CoralWristConstants.*;
import edu.wpi.first.wpilibj2.command.Command;
import com.pathplanner.lib.auto.NamedCommands;

public class CoralWristCommands {
    private final CoralWristSubsystem coralWrist;

    public CoralWristCommands(CoralWristSubsystem wrist) {
        this.coralWrist = wrist;

        NamedCommands.registerCommand("Wrist Coral Station", set(State.STATION));
        NamedCommands.registerCommand("Wrist Upright", set(State.STOWED));
        NamedCommands.registerCommand("Wrist L1-L3", set(State.L1toL3));
        NamedCommands.registerCommand("Wrist L4", set(State.L4));
    }

    public Command set(State state) {
        return coralWrist
                .runOnce(() -> coralWrist.set(state.getPostion()))
                .until(coralWrist::atPosition)
                .handleInterrupt(coralWrist::reset);
    }

    public Command reset() {
        return coralWrist.runOnce(coralWrist::reset);
    }

    public CoralWristSubsystem getCoralWrist() {
        return coralWrist;
    }
}
