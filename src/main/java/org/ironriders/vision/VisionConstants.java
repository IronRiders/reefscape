package org.ironriders.vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;

public class VisionConstants {

    public static final Camera[] CAMERAS = {
            new Camera("front", new Transform3d(new Translation3d(Units.inchesToMeters(14), 0.0, 6.5), new Rotation3d()))
    };

    public static final Pose2d[] STATION_LOCATIONS_RED = { new Pose2d() };
    public static final Pose2d[] STATION_LOCATIONS_BLUE = { new Pose2d() };

    /** represents a name and offset for convenience/simplicity */
    public static class Camera {
        public String name;
        public Transform3d offset;

        public Camera(String name, Transform3d offset) {
            this.name = name;
            this.offset = offset;
        }
    }
}
