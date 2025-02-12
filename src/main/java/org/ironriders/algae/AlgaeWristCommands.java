package org.ironriders.algae;

import org.ironriders.algae.AlgaeWristConstants.*;
import edu.wpi.first.wpilibj2.command.Command;
import com.pathplanner.lib.auto.NamedCommands;

public class AlgaeWristCommands {
    private final AlgaeWristSubsystem algaeWrist;

    public AlgaeWristCommands(AlgaeWristSubsystem wrist) {
        this.algaeWrist = wrist;

        NamedCommands.registerCommand("Algae Wrist Stowed", set(AlgaeWristState.STOWED));
        NamedCommands.registerCommand("Algae Wrist Extended", set(AlgaeWristState.EXTENDED));
    }

    public Command set(AlgaeWristState state) {
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
