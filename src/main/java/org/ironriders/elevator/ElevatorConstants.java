package org.ironriders.elevator;

public class ElevatorConstants {

    public static final int LEFT_MOTOR_ID = 0;
    public static final int RIGHT_MOTOR_ID = 0;

    public static final double GEAR_RATIO = 0;
    public static final double SPROCKET_DIAMETER = 0;

    public static final double HEIGHT_L1 = 0; // inches
    public static final double HEIGHT_L2 = 0;
    public static final double HEIGHT_L3 = 0;
    public static final double HEIGHT_L4 = 0;

    public static final double MOTOR_PID_P = 0;
    public static final double MOTOR_PID_I = 0;
    public static final double MOTOR_PID_D = 0;
    public static final double MOTOR_PID_IZONE = 0;

    public static final double GOAL_TOLERANCE = 0.1; // same unit as heights

    public static final double MAXIMUM_VELOCITY = 0;
    public static final double MAXIMUM_ACCELERATION = 0;

    public static final double T = .2;
    public static final double KS = 0;
    public static final double KG = 0;
    public static final double KV = 0;
    public static final double KA = 0;

    public static final double FF_VEL = 0;
    public static final double FF_ACC = 0;

    public enum Level {
        L1(HEIGHT_L1),
        L2(HEIGHT_L2),
        L3(HEIGHT_L3),
        L4(HEIGHT_L4);

        public double height;

        private Level(double height) {
            this.height = height;
        }
    }
}
