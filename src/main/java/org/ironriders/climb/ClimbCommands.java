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
    }
    
    public Command set(ClimbConstants.State state) {
        return climb
                .runOnce(() -> climb.set(state));
    }

    public Command goTo(ClimbConstants.Targets limit) {
        return climb
            .defer(() -> { 
                System.out.println("(Climber) Going to " + limit); 
                return Commands.none();

            });
    }

    private Command reZero() {
        return climb.runOnce(() -> reZero());
    }
}