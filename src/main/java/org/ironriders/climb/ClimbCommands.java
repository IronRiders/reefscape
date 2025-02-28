package org.ironriders.climb;

import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {
    public final ClimbSubsystem climb;
    public ClimbCommands(ClimbSubsystem climb) {
        this.climb = climb;
        NamedCommands.registerCommand("Climber Grab", set(ClimbConstants.State.DOWN));
        NamedCommands.registerCommand("Climber Release", set(ClimbConstants.State.UP));
        NamedCommands.registerCommand("Climber Release", stopBeingPressed());

    }
    public Command set(ClimbConstants.State state) {
        ClimbConstants.stopPressed = false; // Resets the emergency stop to false
        return climb
                .runOnce(() -> climb.set(state))
                .withTimeout(org.ironriders.climb.ClimbConstants.liftTime)
                .onlyWhile(() -> !ClimbConstants.stopPressed);
    }
    public Command stopBeingPressed () {
        return climb
                .runOnce(() -> ClimbConstants.stopPressed = false);
    }
}