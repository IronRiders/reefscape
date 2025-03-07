package org.ironriders.wrist.algae;

import org.ironriders.wrist.algae.AlgaeWristConstants.*;

import edu.wpi.first.wpilibj2.command.Command;

public class AlgaeWristCommands extends Command {
    private final AlgaeWristSubsystem algaeWrist;

    public AlgaeWristCommands(AlgaeWristSubsystem wrist) {
        this.algaeWrist = wrist;

        wrist.publish("Home", home());
        wrist.publish("Algae Wrist Stowed", set(State.STOWED));
        wrist.publish("Algae Wrist Extended", set(State.EXTENDED));
    }

    public Command home() {
        return algaeWrist.homeCmd().andThen(set(State.STOWED));
    }

    public Command set(State state) {
        return algaeWrist.moveToCmd(state.getAngle());
    }

    public Command reset() {
        return algaeWrist.runOnce(algaeWrist::reset);
    }

    public AlgaeWristSubsystem getAlgaeWrist() {
        return algaeWrist;
    }
}
