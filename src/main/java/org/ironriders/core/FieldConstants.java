package org.ironriders.core;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;

public class FieldConstants {

    private static final AprilTagFieldLayout field = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

    public static final Pose2d TEST = getPose(0);

    private static Pose2d getPose(int id) {
        return field.getTagPose(id).get().toPose2d();
    }
}
