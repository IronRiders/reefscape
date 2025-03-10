package org.ironriders.wrist.coral;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;

public class CoralWristConstants {

    public static final String DASHBOARD_PREFIX = "coralwrist/";

    // motor IDs (-1 = unknown)
    public static final int CORALWRISTMOTOR = 13;

    // Need to tune
    public static final double P = 0.01;
    public static final double I = 0.0;
    public static final double D = 0.0;

    public static final int CORAL_WRIST_CURRENT_STALL_LIMIT = 10; // please test
    public static final double CORAL_WRIST_TOLERANCE = 10; // tune me please

    public static final Angle HOME_ANGLE = Units.Degrees.of(48);

    public static final double GEAR_RATIO = 1/100;

    public static final double t = 0.02;

    public static final double MAX_ACC = 90;
    public static final double MAX_VEL = 90; // Was 180; try to keep from throwing coral

    public enum State {
        STATION(20),
        STOWED(45), // Will stop at limit
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
