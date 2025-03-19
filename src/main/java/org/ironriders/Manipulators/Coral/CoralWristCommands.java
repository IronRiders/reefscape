package org.ironriders.Manipulators.Coral;

import org.ironriders.Manipulators.Coral.CoralWristConstants.CoralWristState;

import edu.wpi.first.wpilibj2.command.Command;

public class CoralWristCommands {
    private final CoralWristSubsystem coralWrist;

    public CoralWristCommands(CoralWristSubsystem wrist) {
        this.coralWrist = wrist;

        wrist.publish("Home", home());
        wrist.publish("Wrist Coral Station", set(CoralWristState.STATION));
        wrist.publish("Wrist Upright", set(CoralWristState.STOWED));
        wrist.publish("Wrist L1-L3", set(CoralWristState.L1toL3));
        wrist.publish("Wrist L4", set(CoralWristState.L4));
    }

    public Command set(CoralWristState state) {
        return coralWrist.moveToCmd(state.getAngle());
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
