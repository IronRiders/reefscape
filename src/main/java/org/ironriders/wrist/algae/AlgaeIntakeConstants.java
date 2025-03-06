package org.ironriders.wrist.algae;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class AlgaeIntakeConstants {

    public static final String DASHBOARD_PREFIX_ALGAE = "algae intake/";

    // motor IDs (-1 = unknow)
    public static final int ALGAELEFTINTAKEMOTOR = 15;
    public static final int ALGAERIGHTINTAKEMOTOR = 16;
    // public static final int ALGAE_LIMITSWITCH = -1;

    public static final int ALGAE_INTAKE_CURRENT_STALL_LIMIT = 1; // FOR TESTING!!!


    public static final int DISCHARGE_TIMEOUT = 1; // these are both in SECONDS
    public static final int INTAKE_IMPATIENCE = 1; // how much time to wait for the limit switch before
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

    public static final TrapezoidProfile.Constraints PROFILE = new TrapezoidProfile.Constraints(500, 850);
}
