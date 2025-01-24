package org.ironriders.manipulators;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class CoralWristConstants {
    
    public static final String DASHBOARD_PREFIX = "coralwrist/";

    //motor IDs (-1 = unknow)
    public static final int CORALWRISTMOTOR = -1;
    public static final int CORALWRISTENCODER = -1;

    // Need to tune
    public static final double CORALWRISTKP = 0.55;
    public static final double CORALWRISTKI = 0;
    public static final double CORALWRISTKD = 0.0;
    // public static final double CORALWRISTKS = 0.0; //The static gain in volts. //
    // Need to test
    // public static final double CORALWRISTKG = 0.0; //The gravity gain in volts.
    // // Need to test
    // public static final double CORALWRISTKV = 0.0; // The velocity gain in
    // V/(rad/s).

    public static final int CORAL_WRIST_CURRENT_STALL_LIMIT = 30; //please test
    public static final int CORAL_WRIST_COMPENSATED_VOLTAGE = 10; // ASK A MENTOR PLEASE
    public static final double CORAL_WRIST_ENCODER_OFFSET = -1; // please test
    public static final double Coral_Wrist_TOLERANCE = 10; // tune me please

    public enum State {
        STATION(0),
        STOWED(0),
        L1toL3(0),
        L4(0);

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
