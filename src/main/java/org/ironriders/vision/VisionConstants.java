package org.ironriders.vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;

public class VisionConstants {
    public static final String CAM_NAME = "apriltagcam";
    public static final String[] CAM_NAMES = { "frontLeft", "frontRight", "backLeft", "backRight", "apriltagcam" };// you can put any
                                                                                                    // number of cameras
                                                                                                    // here
    public static final Transform3d[] CAM_OFFSETS = {};
    public static final Pose2d[] STATION_LOCATIONS_RED = { new Pose2d() };
    public static final Pose2d[] STATION_LOCATIONS_BLUE = { new Pose2d() };
    public static final int[] REEF_TAG_IDS_RED = { 17, 18, 19, 20, 21, 22 };
    public static final int[] REEF_TAG_IDS_BLUE = { 6, 7, 8, 9, 10, 11 };
}
