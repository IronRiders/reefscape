package org.ironriders.core;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FieldConstants {

    public static final double BARGE_YPOS = 0.0; // TODO

    public static final Pose2d STATION_POSE = new Pose2d(); // TODO

    /**
     * Fields:
     * * X position, axis from blue alliance wall to red alliance wall
     * * y Y position, axis from left side to right side (from blue POV)
     * * r Rotation, in radians
     */
    public static enum ReefPosition {
        FRONT(0.0, 0.0, 0.0);

        private final Pose2d pose;

        private ReefPosition(double x, double y, double r) {
            this.pose = new Pose2d(x, y, new Rotation2d(r));
        }

        public Pose2d pose() {
            return pose;
        }
    };
    
    public static enum BargePosition {
        LEFT(new Pose2d()), 
        MIDDLE(new Pose2d()), 
        RIGHT(new Pose2d());

        private final Pose2d pose;

        private BargePosition(double x) {
            this.pose = new Pose2d(x, BARGE_YPOS, new Rotation2d(0.0));
        }

        public Pose2d pose() {
            return pose;
        }
    };
}
