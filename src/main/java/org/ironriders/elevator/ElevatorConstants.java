package org.ironriders.elevator;

public class ElevatorConstants {

    public static final int PRIMARY_MOTOR_ID = 11;
    public static final int FOLLOW_MOTOR_ID = 10;

    public static final double GEAR_RATIO = 0;
    public static final double SPROCKET_DIAMETER = 0;

    public static final double BOTTOM_POS = 0;
    public static final double DOWN_POS = 0;

    public static final double L1_HEIGHT = 0;
    public static final double L2_HEIGHT = 0;
    public static final double L3_HEIGHT = 0;
    public static final double L4_HEIGHT = 0;

    public static final double MIN_POSITION = 0;  //please tune
    public static final double MAX_POSITION = 10; //please tune
    
    public static final double P = 0;
    public static final double I = 0;
    public static final double D = 0;
    public static final double I_ZONE = 0;

    public static final double MAX_VEL = 0;
    public static final double MAX_ACC = 0;
    public static final double MAX_OUTPUT = 5; //please tune me 

    public static final double T = .2;
    public static final double K_S = 0;
    public static final double K_G = 0;
    public static final double K_V = 0;
    public static final double K_A = 0;

    public static final double FF_VEL = 0;
    public static final double FF_ACC = 0;
    public static final double INCHES_PER_ROTATION = 1; // please tune me pls pls pls 

    public enum Level {
        Down (DOWN_POS),
        L1 (L1_HEIGHT),
        L2 (L2_HEIGHT),
        L3 (L3_HEIGHT),
        L4 (L4_HEIGHT);
    
        public double positionInches;
    
        private Level(double positionInches){
            this.positionInches = positionInches;
        }
    }
}
