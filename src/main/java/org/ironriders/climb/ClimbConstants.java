package org.ironriders.climb;

public class ClimbConstants {
    public static double liftTime = 3;
    public static boolean stopPressed = false;
    public static final int CLIMBER_MOTOR = 17; // probably what the ID will be, but we'll see when it's assigned TODO
    
    public enum State {
        UP(0.1),
        DOWN(-0.1);

        private final double speed;

        State(double speed) {
            this.speed = speed;
        }

        public double getSpeed() {
            return speed;
        }
    }
}
