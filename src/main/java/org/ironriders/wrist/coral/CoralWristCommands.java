package org.ironriders.wrist.coral;

import org.ironriders.wrist.coral.CoralWristConstants.*;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class CoralWristCommands {
    private final CoralWristSubsystem coralWrist;

    public CoralWristCommands(CoralWristSubsystem wrist) {
        this.coralWrist = wrist;

        wrist.publish("Home", home());
        wrist.publish("Wrist Coral Station", set(State.STATION));
        wrist.publish("Wrist Upright", set(State.STOWED));
        wrist.publish("Wrist L1-L3", set(State.L1toL3));
        wrist.publish("Wrist L4", set(State.L4));
    }

    public Command set(State state) {
        return coralWrist.moveToCmd(state.getAngle()).andThen(Commands.waitUntil(() -> { return coralWrist.atPosition(); }));
    }

    public Command reset() {
        return coralWrist.runOnce(coralWrist::reset);
    }

    public Command home() {
        return coralWrist.homeCmd();
    }

    public CoralWristSubsystem getCoralWrist() {
        return coralWrist;
    }
}
