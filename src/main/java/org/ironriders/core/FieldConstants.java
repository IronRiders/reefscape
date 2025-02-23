package org.ironriders.core;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;

public class FieldConstants {

    public static final AprilTagFieldLayout FIELD_LAYOUT = AprilTagFieldLayout.loadField(AprilTagFields.k2025ReefscapeAndyMark);

    public static Pose2d getPose(int id) {
        return FIELD_LAYOUT.getTagPose(id).get().toPose2d();
    }
}
