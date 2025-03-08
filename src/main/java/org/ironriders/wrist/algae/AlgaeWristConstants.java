package org.ironriders.wrist.algae;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;

public class AlgaeWristConstants {

    public static final String DASHBOARD_PREFIX = "algaewrist/";

    // motor ID
    public static final int ALGAEWRISTMOTOR = 14;

    // Need to tune
    public static final double P = 0.01;
    public static final double I = 0.01;
    public static final double D = 0.0;
    
    public static final double t = 0.02;
    public static final double SPROCKET_RATIO=(13/12);
    public static final double GEAR_RATIO = 1.0 / 64.0 * SPROCKET_RATIO;
    public static final double ENCODER_SCALE = SPROCKET_RATIO;
    public static final Angle ENCODER_OFFSET = Units.Degrees.of(135);
    public static final Angle REVERSE_LIMIT = Units.Degrees.of(-50);
    public static final Angle FORWARD_LIMIT = Units.Degrees.of(5);
    public static final double MAX_ACC = 90;
    public static final double MAX_VEL = 45; //90;

    // public static final double ALGAEWRISTKS = 0.0; //The static gain in volts. //
    // Need to test
    // public static final double ALGAEWRISTKG = 0.0; //The gravity gain in volts.
    // // Need to test
    // public static final double ALGAEWRISTKV = 0.0; // The velocity gain in
    // V/(rad/s).

    public static final int ALGAE_WRIST_CURRENT_STALL_LIMIT = 20; 

    public static final double ALGAE_WRIST_TOLERENCE = 10; // tune me please

    public enum State { // max seems to be 40, min ~0
        STARTING(-90), // Starting position is beyond lower limit, exact position unknown
        STOWED(-45), // Computed home position
        EXTENDED(0);

        final Angle angle;

        State(double degrees) {
            this.angle = Units.Degrees.of(degrees);
        }

        public Angle getAngle() {
            return angle;
        }
    }

    public static final TrapezoidProfile.Constraints PROFILE = new TrapezoidProfile.Constraints(500, 850);
}
