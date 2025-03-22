package org.ironriders.climb;
import org.ironriders.climb.ClimbConstants.State;

import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {
    public final ClimbSubsystem climb;


    public ClimbCommands(ClimbSubsystem climb) {
        this.climb = climb;

        climb.publish("Climb Up", set(ClimbConstants.State.UP));
        climb.publish("Climb Down", set(ClimbConstants.State.DOWN));
    }

    public Command set(ClimbConstants.State state) {
        return climb
            .runEnd(() -> climb.set(state), ()->climb.set(State.STOP));
    }

    public Command rezero(){
        return climb.runOnce(() -> climb.rezero());
    }

}