package org.ironriders.wrist.coral;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;

public class CoralWristConstants {

    public static final String DASHBOARD_PREFIX = "coralwrist/";

    // motor IDs (-1 = unknow)
    public static final int CORALWRISTMOTOR = 13;

    // Need to tune
    public static final double CORALWRISTKP = .05;
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

    public static final Angle HOME_ANGLE = Units.Degrees.of(48);

    public static final double GEAR_RATIO = 0.01;

    public static final double t = 0.02;

    public static final double MAX_ACC = 90; //90;
    public static final double MAX_VEL = 180; //180;

    public enum State {
        STATION(32.5),
        STOWED(90), // Will stop at limit
        L1toL3(0),
        L4(-30);

        final Angle angle;

        State(double degrees) {
            this.angle = Units.Degrees.of(degrees);
        }

        public Angle getAngle() {
            return angle;
        }
    }
}
