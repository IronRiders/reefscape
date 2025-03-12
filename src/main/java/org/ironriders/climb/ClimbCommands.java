package org.ironriders.climb;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class ClimbCommands {
    public final ClimbSubsystem climb;


    public ClimbCommands(ClimbSubsystem climb) {
        this.climb = climb;

        climb.publish("Climb to MAX", goTo(ClimbConstants.Targets.MAX));
        climb.publish("Climb to HOME", goTo(ClimbConstants.Targets.HOME));
        climb.publish("Climb to TARGET", goTo(ClimbConstants.Targets.TARGET));
        climb.publish("Re-zero (TESTING ONLY)", reZero());
        climb.publish("Force up (TESTING ONLY)", moveSeconds(ClimbConstants.State.UP, 1));
        climb.publish("Force down (TESTING ONLY)", moveSeconds(ClimbConstants.State.DOWN, 1));
    }

    public Command goTo(ClimbConstants.Targets limit) {
        return climb
            .runOnce(() -> climb.goTo(limit));
    }

    private Command reZero() {
        return climb.runOnce(() -> climb.reZero());
    }

    public Command getGoalpoint() {
        return climb.runOnce(() -> climb.getGoal());
    }

    public Command moveSeconds(ClimbConstants.State state, double seconds) {
        return climb.runOnce(() -> climb.set(state)).andThen(Commands.waitSeconds(seconds)).andThen(() -> climb.set(ClimbConstants.State.STOP));
    }
}