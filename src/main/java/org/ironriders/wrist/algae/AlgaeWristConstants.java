package org.ironriders.wrist.algae;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import java.lang.Math;

public class AlgaeWristConstants {

    public static final int ALGAE_WRIST_MOTOR = 14;

    // Need to tune
    public static final double P = 0.01;
    public static final double I = 0.01;
    public static final double D = 0.0;

    public static final double SPROCKET_RATIO = (13 / 12);
    public static final double GEAR_RATIO = 1.0 / 64.0 * SPROCKET_RATIO;
    public static final double ENCODER_SCALE = SPROCKET_RATIO;


    public static final Angle ENCODER_OFFSET = Units.Degrees.of(110);
    public static final Angle REVERSE_LIMIT = Units.Degrees.of(-50);
    public static final Angle FORWARD_LIMIT = Units.Degrees.of(5);

    public static final int TARGET_OFFSET = 0;
    public static final double TARGET_STOW = Math.subtractExact(-40, TARGET_OFFSET);
    public static final double TARGET_EXTEND = Math.subtractExact(0, TARGET_OFFSET);


    public static final double MAX_ACC = 45;
    public static final double MAX_VEL = 45;

    public static final int ALGAE_WRIST_CURRENT_STALL_LIMIT = 35;
    public static final double ALGAE_WRIST_TOLERANCE = 1; // tune me please

    public enum AlgaeWristState {
        STARTING(-60), // Starting position is beyond lower limit, exact position unknownn
        STOWED(TARGET_STOW), // Computed home position
        EXTENDED(TARGET_EXTEND);

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
