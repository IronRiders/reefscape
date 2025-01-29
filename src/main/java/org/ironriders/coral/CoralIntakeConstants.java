package org.ironriders.coral;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class CoralIntakeConstants {
    
    public static final String DASHBOARD_PREFIX_CORAL = "coral intake/";
    public static final String DASHBOARD_PREFIX_ALGAE = "algae intake/";

    //motor IDs (-1 = unknow)
    public static final int CORAL_INTAKE_MOTOR = -1;
    public static final int CORAL_INTAKE_CURRENT_STALL_LIMIT = 30; //please test
    public static final int ALGAE_INTAKE_CURRENT_STALL_LIMIT = 30; //please test
    public static final int CORAL_INTAKE_COMPENSATED_VOLTAGE = 10; // ASK A MENTOR PLEASE
    public static final int ALGAE_INTAKE_COMPENSATED_VOLTAGE = 10; // ASK A MENTOR PLEASE

    public static final int DISCHARGE_TIMEOUT = 1;


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
