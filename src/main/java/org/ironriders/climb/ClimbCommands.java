package org.ironriders.climb;
import edu.wpi.first.wpilibj2.command.Command;

public class ClimbCommands {
    public final ClimbSubsystem climb;


    public ClimbCommands(ClimbSubsystem climb) {
        this.climb = climb;

        climb.publish("Climb to MAX", goTo(ClimbConstants.Targets.MAX));
        climb.publish("Climb to HOME", goTo(ClimbConstants.Targets.HOME));
        climb.publish("Climb to TARGET", goTo(ClimbConstants.Targets.TARGET));
        climb.publish("Re-zero (TESTING ONLY)", reZero());
    }

    public Command goTo(ClimbConstants.Targets limit) {
        return climb
            .runOnce(() -> climb.goTo(limit));
    }
    
    public Command setMotor(double speed) {
        return climb.runOnce(() -> climb.set(speed));
    }
    private Command reZero() {
        return climb.runOnce(() -> climb.reZero());
    }

    public Command getGoalpoint() {
        return climb.runOnce(() -> climb.getGoal());
    }
}