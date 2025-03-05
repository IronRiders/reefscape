package org.ironriders.lib;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;

/**
 * Fully specified robot pose relative to a field element.
 */
public class FieldPose {
    static final Distance ROBOT_LENGTH = Units.Inches.of(39);
    static final Distance CORAL_INTAKE_OFFSET = Units.Inches.of(7);
    static final Distance STATION_SLOT_SPACING = Units.Inches.of(8);
    static final int STATION_SLOT_COUNT = 9;
    static final Distance REEF_POLE_SPACING = Units.Inches.of(12.94);

    /**
     * The element targeted.
     */
    public final FieldElement element;

    /**
     * Target slot (0-8).  Only affects targeting of coral station.  Slot 0 is closest to driver.
     */
    public final int slot;

    /**
     * Target side.  Only affects targeting of reef.
     */
    public final Side side;

    /**
     * Target level.  Only affects targeting of reef.
     */
    public final Level level;

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

    static private int activeSlot = STATION_SLOT_COUNT / 2;
    static private Side activeSide = Side.Left;
    static private Level activeLevel = Level.L1;

    public FieldPose(FieldElement element) {
        this.element = element;
        this.slot = activeSlot;
        this.side = activeSide;
        this.level = activeLevel;
    }

    public FieldPose(FieldElement element, int slot) {
        this.element = element;
        this.slot = slot;
        this.side = activeSide;
        this.level = activeLevel;
    }

    public FieldPose(FieldElement element, Side side, Level level) {
        this.element = element;
        this.slot = activeSlot;
        this.side = side;
        this.level = level;
    }

    static void setActiveSlot(int slot) {
        activeSlot = slot;
    }

    static int getActiveSlot() {
        return activeSlot;
    }

    static void setActiveSide(Side side) {
        activeSide = side;
    }

    static Side getActiveSide() {
        return activeSide;
    }

    static void setActiveLevel(Level level) {
        activeLevel = level;
    }

    static Level getActivelevel() {
        return activeLevel;
    }

    /**
     * Create a concrete Pose2d relative to this abstract pose.
     */
    public Pose2d toPose2d() {
        final var elementPose = this.element.pose.toPose2d();

        Distance xOffset;
        if (this.element.type == FieldElement.ElementType.STATION) {
            xOffset = STATION_SLOT_SPACING.times(slot - STATION_SLOT_COUNT / 2).minus(CORAL_INTAKE_OFFSET.div(2));
        } else if (this.element.type == FieldElement.ElementType.PROCESSOR) {
            xOffset = REEF_POLE_SPACING.div(side == Side.Left ? 2 : -2);
        } else {
            xOffset = Units.Inches.of(0);
        }

        final var relativeTranslation = new Translation2d(xOffset, ROBOT_LENGTH.div(-2));
        relativeTranslation.rotateBy(elementPose.getRotation());

        final var robotTranslation = elementPose.getTranslation().plus(relativeTranslation);

        final var robotRotation = elementPose.getRotation().rotateBy(Rotation2d.k180deg);

        return new Pose2d(robotTranslation, robotRotation);
    }
}
