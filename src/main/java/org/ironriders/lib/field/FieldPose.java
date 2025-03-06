package org.ironriders.lib.field;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;

/**
 * Fully specified robot pose relative to a field element.
 */
public class FieldPose {
    /**
     * Field pose at coral station.
     */
    public static class Station extends FieldPose {
        /**
         * Target slot (0-8).  Slot 0 is closest to driver.
         */
        public final int slot;

        public Station(FieldElement element, int slot) {
            super(element);
            this.slot = slot;
        }

        @Override
        protected Distance getYOffset() {
            return STATION_SLOT_SPACING.times(slot - STATION_SLOT_COUNT / 2).minus(CORAL_INTAKE_OFFSET.div(2));
        }
    }

    /**
     * Field pose at reef.
     */
    public static class Reef extends FieldPose {
        /**
         * Target pole.  Only affects targeting of reef.
         */
        public final Side pole;

        /**
         * Target level.  Only affects targeting of reef.
         */
        public final Level level;

        public Reef(FieldElement element, Side pole, Level level) {
            super(element);
            this.pole = pole;
            this.level = level;
        }

        @Override
        protected Distance getYOffset() {
            return REEF_POLE_SPACING.div(pole == Side.Left ? 2 : -2);
        }
    }
    
    static final Distance ROBOT_LENGTH = Units.Inches.of(37);
    static final Distance CORAL_INTAKE_OFFSET = Units.Inches.of(7);
    static final Distance STATION_SLOT_SPACING = Units.Inches.of(8);
    static public final int STATION_SLOT_COUNT = 9;
    static final Distance REEF_POLE_SPACING = Units.Inches.of(12.94);

    /**
     * The element targeted.
     */
    public final FieldElement element;

    /**
     * Robot side (robot-relative left or right).
     */
    public enum Side {
        Left,
        Right,
    }

    /**
     * Reef levels.
     */
    public enum Level {
        L1,
        L2,
        L3,
        L4,
    }

    public FieldPose(FieldElement element) {
        this.element = element;
    }

    /**
     * Create a concrete Pose2d relative to this abstract pose.
     */
    public Pose2d toPose2d() {
        final var elementPose = this.element.pose.toPose2d();

        final var robotRotation = elementPose.getRotation().rotateBy(Rotation2d.k180deg);

        final var zeroAngleRelativeTranslation = new Translation2d(ROBOT_LENGTH.div(-2), getYOffset());

        final var relativeTranslation = zeroAngleRelativeTranslation.rotateBy(robotRotation);

        final var robotTranslation = elementPose.getTranslation().plus(relativeTranslation);

        return new Pose2d(robotTranslation, robotRotation);
    }

    protected Distance getYOffset() {
        return Units.Inches.of(0);
    }
}
