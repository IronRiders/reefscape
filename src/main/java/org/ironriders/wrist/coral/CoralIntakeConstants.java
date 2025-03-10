package org.ironriders.wrist.coral;


public class CoralIntakeConstants {

    public static final String DASHBOARD_PREFIX_CORAL = "coral intake/";

    // motor IDs (-1 = unknow)
    public static final int CORAL_INTAKE_MOTOR = 12;
    public static final int CORAL_INTAKE_CURRENT_STALL_LIMIT = 10; // please test

    public static final double DISCHARGE_TIMEOUT = 2.5; // these are both in SECONDS
    public static final double INTAKE_IMPATIENCE = 7.5; // how much time to wait for the limit switch before
                                                   // stopping the motor anyway

    public static final double MAX_ACC =.1;
    public static final double MAX_VEL = .1;

    public enum State {
        GRAB(.25),
        EJECT(-.25),
        STOP(0);

        private final double speed;

        State(double speed) {
            this.speed = speed;
        }

        public double getSpeed() {
            return speed;
        }

    }
}
