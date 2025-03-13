package org.ironriders.wrist.coral;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;

public class CoralWristConstants {

    public static final int CORAL_WRIST_MOTOR = 13;

    // Need to tune
    public static final double P = 0.01;
    public static final double I = 0.0;
    public static final double D = 0.0;

    public static final double SPROCKET_RATIO = 1;
    public static final double GEAR_RATIO = 1.0 / 100.0;
    public static final double ENCODER_SCALE = SPROCKET_RATIO;

    public static final Angle ENCODER_OFFSET = Units.Degrees.of(42.8);
    public static final Angle REVERSE_LIMIT = Units.Degrees.of(-35); // TODO: TUNE
    public static final Angle FORWARD_LIMIT = Units.Degrees.of(50); // TODO: TUNE

    public static final double MAX_ACC = 90;
    public static final double MAX_VEL = 90;

    public static final int CORAL_WRIST_CURRENT_STALL_LIMIT = 10;
    public static final double CORAL_WRIST_TOLERANCE = 10; // tune me please

    public enum CoralWristState {
        STATION(20),
        STOWED(45), // Will stop at limit
        L1toL3(0),
        L4(-30);

        final Angle angle;

        CoralWristState(double degrees) {
            this.angle = Units.Degrees.of(degrees);
        }

        public Angle getAngle() {
            return angle;
        }
    }
}
