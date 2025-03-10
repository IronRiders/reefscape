package org.ironriders.wrist.algae;

public class AlgaeIntakeConstants {
    // motor IDs (-1 = unknown)
    public static final int ALGAE_LEFT_ID = 15;
    public static final int ALGAE_RIGHT_ID = 16;
    // public static final int ALGAE_LIMITSWITCH = -1;

    public static final int ALGAE_INTAKE_CURRENT_STALL_LIMIT = 2; // FOR TESTING!!!

    public static final int EJECT_TIMEOUT = 1; // these are both in SECONDS
    public static final int INTAKE_TIMEOUT = 1; // how much time to wait for the limit switch before
                                                // stopping the motor anyway

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
}
