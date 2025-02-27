package org.ironriders.algae;

import org.ironriders.algae.AlgaeWristConstants.*;
import edu.wpi.first.wpilibj2.command.Command;
import com.pathplanner.lib.auto.NamedCommands;

public class AlgaeWristCommands extends Command {
    private final AlgaeWristSubsystem algaeWrist;

    public AlgaeWristCommands(AlgaeWristSubsystem wrist) {
        this.algaeWrist = wrist;

        NamedCommands.registerCommand("Algae Wrist Stowed", set(State.STOWED));
        NamedCommands.registerCommand("Algae Wrist Extended", set(State.EXTENDED));
    }

    public Command set(State state) {
        return algaeWrist
                .runOnce(() -> algaeWrist.setGoal(state.getPostion()))
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
