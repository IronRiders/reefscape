package org.ironriders.wrist.algae;

import org.ironriders.wrist.algae.AlgaeWristConstants.AlgaeWristState;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

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
        //return Commands.none();
    }

    public Command set(AlgaeWristState state) {
        return algaeWrist.moveToCmd(state.getAngle());
        //return Commands.none();
    }

    public Command reset() {
        return algaeWrist.runOnce(algaeWrist::reset);
        //return Commands.none();
    }

    public AlgaeWristSubsystem getAlgaeWrist() {
        return algaeWrist;
    }
}
