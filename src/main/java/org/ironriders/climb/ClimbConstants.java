package org.ironriders.climb;

public class ClimbConstants {

    public static final double LIFT_TIME = 3;
    public static final int CLIMBER_MOTOR_CAN_ID = 17;
    public static final int COMPENSATION = 12;
    public static final int CURRENT_LIMIT = 40;
    public static final int FAKE_PID_COMPENSATION = 1; //TODO: TUNE
    public static final double MAX_POSITION = 50;
    public static final double GEAR_RATIO = 0.01;
    //public static final double CLIMBER_LIMIT = 45;
    
    public enum State {
        UP(-0.3),
        STOP(0),
        DOWN(0.3);

        public final double speed;

        State(double speed) { this.speed = speed; }
    }
}
