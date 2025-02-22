package org.ironriders.coral;

import org.dyn4j.geometry.Rotation;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class CoralWristConstants {

    public static final String DASHBOARD_PREFIX = "coralwrist/";

    // motor IDs (-1 = unknow)
    public static final int CORALWRISTMOTOR = 13;

    // Need to tune
    public static final double CORALWRISTKP = 0.025;
    public static final double CORALWRISTKI = 0;
    public static final double CORALWRISTKD = 0.0;
    // public static final double CORALWRISTKS = 0.0; //The static gain in volts. //
    // Need to test
    // public static final double CORALWRISTKG = 0.0; //The gravity gain in volts.
    // // Need to test
    // public static final double CORALWRISTKV = 0.0; // The velocity gain in
    // V/(rad/s).

    public static final int CORAL_WRIST_CURRENT_STALL_LIMIT = 10; // please test
    // public static final double CORAL_WRIST_ENCODER_OFFSET = 0; // please test
    public static final double CORAL_WRIST_TOLERANCE = 10; // tune me please

    public static final double MAX_POSITION = 10;
    public static final double MIN_POSITION = -100;

    public static final double GEAR_RATIO = 0.01;

    public static final double t = 0.02;


    public enum State {
        STATION(-20),
        STOWED(0),
        L1toL3(-45),
        L4(-65);

        final double postion;

        State(int postion) {
            this.postion = postion;
        }

        public double getPostion() {
            return postion;
        }

    }

    public static final double MAX_ACC = 90;
    public static final double MAX_VEL = 180;
}
