package org.ironriders.vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;

public class VisionConstants {

    public static final Camera[] CAMERAS = {
            new Camera("front", new Transform3d()),
            new Camera("frontLeft", new Transform3d()),
            new Camera("frontRight", new Transform3d()),
            new Camera("backLeft", new Transform3d()),
            new Camera("backRight", new Transform3d())
    };

    public static final Pose2d[] STATION_LOCATIONS_RED = { new Pose2d() };
    public static final Pose2d[] STATION_LOCATIONS_BLUE = { new Pose2d() };

    public static final int[] REEF_TAG_IDS_RED = { 17, 18, 19, 20, 21, 22 };
    public static final int[] REEF_TAG_IDS_BLUE = { 6, 7, 8, 9, 10, 11 };

    public static class Camera {
        public String name;
        public Transform3d offset;

        public Camera(String name, Transform3d offset) {
            this.name = name;
            this.offset = offset;
        }
    }
}
