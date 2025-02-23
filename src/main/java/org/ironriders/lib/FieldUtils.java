package org.ironriders.lib;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class FieldUtils {

    public static final AprilTagFieldLayout FIELD_LAYOUT = AprilTagFieldLayout.loadField(AprilTagFields.k2025ReefscapeAndyMark);

    public static final int[] REEF_TAG_IDS_RED = { 17, 18, 19, 20, 21, 22 };
    public static final int[] REEF_TAG_IDS_BLUE = { 6, 7, 8, 9, 10, 11 };

    public static boolean isValidReefTag(int id) {
        for (int i : DriverStation.getAlliance().get() == Alliance.Blue ? REEF_TAG_IDS_RED : REEF_TAG_IDS_BLUE) {
            if (i == id)
                return true;
        }
        return false;
    }

    public static Pose2d getPose(int id) {
        return FIELD_LAYOUT.getTagPose(id).get().toPose2d();
    }
}
