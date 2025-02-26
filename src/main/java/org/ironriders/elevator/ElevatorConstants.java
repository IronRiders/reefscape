package org.ironriders.elevator;

public class ElevatorConstants {

    public static final int PRIMARY_MOTOR_ID = 11;
    public static final int FOLLOW_MOTOR_ID = 10;

    public static final int ELEVATOR_MOTOR_STALL_LIMIT =   30; //in amps and applies to both primary and follower

    public static final double BOTTOM_POS = 0; //Homed postion

    public static final double DOWN_POS = 0; // Elevator at min postion
    public static final double CORAL_STATION_POS = 8.15;
    public static final double L1_HEIGHT = 4;
    public static final double L2_HEIGHT = 6;
    public static final double L3_HEIGHT = 20;
    public static final double L4_HEIGHT = 53;


    public static final double MIN_POSITION = 0;  //please tune
    public static final double MAX_POSITION = 53; //please tune ASK JIM (finn said it was 54) 53 is the safe tested number unless limit switch is working
    
    public static final double P = 0.55; // Mostly good
    public static final double I = 0.15;
    public static final double D = 0;

    public static final double MAX_VEL = 40;
    public static final double MAX_ACC = 80;
    public static final double MAX_OUTPUT = 5; // please tune me tooooooo!!!!

    public static final double T = .02;
    public static final double K_S = 0;
    public static final double K_G = 0;
    public static final double K_V = 0;
    public static final double K_A = 0;

    public static final double FF_VEL = 0;
    public static final double FF_ACC = 0;

    public static final double GEAR_RATIO = 0.05;
    public static final double SPROCKET_DIAMETER = 1.7567;
    public static final int NUMBER_OF_STAGES = 2;
    public static final double SCALEFACTOR =1;
    public static final double INCHES_PER_ROTATION = GEAR_RATIO * SPROCKET_DIAMETER * Math.PI * NUMBER_OF_STAGES * SCALEFACTOR; // please tune me pls pls pls 

    public enum Level {
        Down (DOWN_POS),
        CoralStation (CORAL_STATION_POS),
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
