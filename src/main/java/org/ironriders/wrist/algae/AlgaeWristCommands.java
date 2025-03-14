package org.ironriders.wrist.algae;

import org.ironriders.wrist.algae.AlgaeWristConstants.AlgaeWristState;

import edu.wpi.first.wpilibj2.command.Command;

public class AlgaeWristCommands extends Command {
    private final AlgaeWristSubsystem algaeWrist;

    public AlgaeWristCommands(AlgaeWristSubsystem wrist) {
        this.algaeWrist = wrist;

        wrist.publish("Home", home());
        wrist.publish("Algae Wrist Stowed", set(AlgaeWristState.STOWED));
        wrist.publish("Algae Wrist Extended", set(AlgaeWristState.EXTENDED));
    }

    public Command home() {
        return algaeWrist.homeCmd();
    }

    public Command set(AlgaeWristState state) {
        return algaeWrist.moveToCmd(state.getAngle());
    }

    public Command reset() {
        return algaeWrist.runOnce(algaeWrist::reset);
    }

    public AlgaeWristSubsystem getAlgaeWrist() {
        return algaeWrist;
    }
}
