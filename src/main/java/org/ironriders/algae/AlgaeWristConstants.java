package org.ironriders.algae;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class AlgaeWristConstants {

    public static final String DASHBOARD_PREFIX = "algaewrist/";

    // motor IDs (-1 = unknow)
    public static final int ALGAEWRISTMOTOR = 14;

    // Need to tune
    public static final double P = 0.05;
    public static final double I = 0.05;
    public static final double D = 0.0;
    public static final double t = 0.02;
    public static final double SPROCKET_RATIO=(13/12);
    public static final double GEAR_RATIO = 1/64 * SPROCKET_RATIO;
    public static final double MIN_POSITION = 0;
    public static final double MAX_POSITION = 100;
    public static final double MAX_ACC = 120;
    public static final double MAX_VEL = 60;


    // public static final double ALGAEWRISTKS = 0.0; //The static gain in volts. //
    // Need to test
    // public static final double ALGAEWRISTKG = 0.0; //The gravity gain in volts.
    // // Need to test
    // public static final double ALGAEWRISTKV = 0.0; // The velocity gain in
    // V/(rad/s).

    public static final int ALGAE_WRIST_CURRENT_STALL_LIMIT = 20; 

    public static final double ALGAE_WRIST_TOLERENCE = 10; // tune me please

    public enum State { // max seems to be 40, min ~0
        STARTING(0),
        STOWED(0), // tune me pls
        EXTENDED(30);


        final double postion;

        State(int postion) {
            this.postion = postion;
        }

        public double getPostion() {
            return postion;
        }
    }

    public static final TrapezoidProfile.Constraints PROFILE = new TrapezoidProfile.Constraints(500, 850);
}
