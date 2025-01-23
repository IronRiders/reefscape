package org.ironriders.manipulators;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class IntakeConstants {
    
    public static final String DASHBOARD_PREFIX = "intake/";

    //motor IDs (-1 = unknow)
    public static final int CORALINTAKEMOTOR = -1;
    public static final int ALGAELEFTINTAKEMOTOR = -1;
    public static final int ALGAERIGHTINTAKEMOTOR = -1;


    public static final int CORAL_INTAKE_CURRENT_STALL_LIMIT = 30; //please test
    public static final int ALGAE_INTAKE_CURRENT_STALL_LIMIT = 30; //please test
    public static final int CORAL_INTAKE_COMPENSATED_VOLTAGE = 10; // ASK A MENTOR PLEASE
    public static final int ALGAE_INTAKE_COMPENSATED_VOLTAGE = 10; // ASK A MENTOR PLEASE


    public enum State {
        GRAB(1),
        EJECT(-1),
        STOP(0);

        private final double speed;

        State(double speed) {
            this.speed = speed;
        }
        public double getSpeed() {
            return speed;
        }

    }

    public static final TrapezoidProfile.Constraints PROFILE =
                new TrapezoidProfile.Constraints(500, 850);
}
