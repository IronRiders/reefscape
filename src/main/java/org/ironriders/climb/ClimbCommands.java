package org.ironriders.climb;
import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {
    public final ClimbSubsystem climb;

    public ClimbCommands(ClimbSubsystem climb) {
        this.climb = climb;
    }
    
    public Command set(ClimbConstants.State state) {
        return climb
                .runOnce(() -> climb.set(state));
    }
}