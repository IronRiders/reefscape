package org.ironriders.climb;
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
            .runOnce(() -> climb.set(state));
    }

    public Command rezero(){
        return climb.runOnce(() -> climb.rezero());
    }

}