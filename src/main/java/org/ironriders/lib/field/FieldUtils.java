package org.ironriders.lib.field;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * Utility class for the field layout and tag information.
 */
public class FieldUtils {

    public static final AprilTagFieldLayout FIELD_LAYOUT = AprilTagFieldLayout
            .loadField(AprilTagFields.k2025ReefscapeAndyMark);

    public static final int[] REEF_TAG_IDS_RED = { 17, 18, 19, 20, 21, 22 };
    public static final int[] REEF_TAG_IDS_BLUE = { 6, 7, 8, 9, 10, 11 };

    public static final Transform2d REEFSIDE_LEFT_OFFSET = createOffset(-14, 0, 0);
    public static final Transform2d REEFSIDE_RIGHT_OFFSET = createOffset(-14, 13, 0);

    /** Checks if a tag is valid for the reef and the current alliance. */
    public static boolean isValidReefTag(int id) {
        for (int i : DriverStation.getAlliance().get() == Alliance.Blue ? REEF_TAG_IDS_RED : REEF_TAG_IDS_BLUE) {
            if (i == id)
                return true;
        }
        return false;
    }

    public static Pose2d applyOffset(Pose2d pose, Transform2d offset) {
        return pose.transformBy(offset);
    }

    /** Creates basic offset Pose2d from x, y, and rotation */
    public static Transform2d createOffset(double x, double y, double r) {
        return new Transform2d(new Translation2d(Units.inchesToMeters(x), Units.inchesToMeters(y)), new Rotation2d(r));
    }

    /**
     * Gets the pose of a tag on the field (0, 0 is the right-close corner from
     * perspective of blue)
     */
    public static Pose2d getPose(int id) {
        return FIELD_LAYOUT.getTagPose(id).get().toPose2d();
    }
}
