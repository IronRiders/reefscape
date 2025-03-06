package org.ironriders.climb;

import org.ironriders.climb.ClimbConstants.State;

import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {
    public final ClimbSubsystem climb;

    public ClimbCommands(ClimbSubsystem climb) {
        this.climb = climb;

        NamedCommands.registerCommand("Climber Down", set(ClimbConstants.State.DOWN));
        NamedCommands.registerCommand("Climber Up", set(ClimbConstants.State.UP));

    }
    
    public Command set(ClimbConstants.State state) {
        return climb
                .runOnce(() -> climb.set(State.UP));
    }
}