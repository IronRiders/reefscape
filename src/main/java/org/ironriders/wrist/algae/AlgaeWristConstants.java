package org.ironriders.wrist.algae;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;

public class AlgaeWristConstants {

    public static final int ALGAE_WRIST_MOTOR = 14;

    // Need to tune
    public static final double P = 0.01;
    public static final double I = 0.01;
    public static final double D = 0.0;

    public static final double SPROCKET_RATIO = (13 / 12);
    public static final double GEAR_RATIO = 1.0 / 64.0 * SPROCKET_RATIO;
    public static final double ENCODER_SCALE = SPROCKET_RATIO;

    public static final Angle ENCODER_OFFSET = Units.Degrees.of(115);
    public static final Angle REVERSE_LIMIT = Units.Degrees.of(-50);
    public static final Angle FORWARD_LIMIT = Units.Degrees.of(5);

    public static final double MAX_ACC = 45;
    public static final double MAX_VEL = 45;

    public static final int ALGAE_WRIST_CURRENT_STALL_LIMIT = 30;
    public static final double ALGAE_WRIST_TOLERANCE = 2; // tune me please

    public enum AlgaeWristState {
        STARTING(-90), // Starting position is beyond lower limit, exact position unknownn
        STOWED(-40), // Computed home position
        EXTENDED(10);

        final Angle angle;

        AlgaeWristState(double degrees) {
            this.angle = Units.Degrees.of(degrees);
        }

        public Angle getAngle() {
            return angle;
        }
    }

    public static final TrapezoidProfile.Constraints PROFILE = new TrapezoidProfile.Constraints(500, 850);
}
