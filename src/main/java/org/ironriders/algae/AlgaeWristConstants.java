package org.ironriders.algae;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class AlgaeWristConstants {
    
    public static final String DASHBOARD_PREFIX = "algaewrist/";

    //motor IDs (-1 = unknow)
    public static final int ALGAEWRISTMOTOR = -1;
    public static final int ALGAEWRISTENCODER = -1;

    // Need to tune
    public static final double ALGAEWRISTKP = 0.55;
    public static final double ALGAEWRISTKI = 0;
    public static final double ALGAEWRISTKD = 0.0;
    // public static final double ALGAEWRISTKS = 0.0; //The static gain in volts. //
    // Need to test
    // public static final double ALGAEWRISTKG = 0.0; //The gravity gain in volts.
    // // Need to test
    // public static final double ALGAEWRISTKV = 0.0; // The velocity gain in
    // V/(rad/s).

    public static final int ALGAE_WRIST_CURRENT_STALL_LIMIT = 30; //please test
    public static final int ALGAE_WRIST_COMPENSATED_VOLTAGE = 10; // ASK A MENTOR PLEASE
    public static final double ALGAE_WRIST_ENCODER_OFFSET = -1; // please test
    public static final double ALGAE_WRIST_TOLERENCE = 10; // tune me please

    public enum State {
        STOWED(0), // tune me pls
        INTAKING(0);


        final double postion;

        State(int postion) {
            this.postion = postion;
        }

        public double getPostion() {
            return postion;
        }

    }


    public static final TrapezoidProfile.Constraints PROFILE =
                new TrapezoidProfile.Constraints(500, 850);
}
