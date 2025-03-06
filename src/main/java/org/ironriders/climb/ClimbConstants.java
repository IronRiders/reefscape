package org.ironriders.climb;

public class ClimbConstants {

    public static final double LIFT_TIME = 3;
    public static final int CLIMBER_MOTOR_CAN_ID = 17;
    
    public enum State {
        UP(0.1),
        DOWN(-0.1);

        public final double speed;

        State(double speed) { this.speed = speed; }
    }
}
