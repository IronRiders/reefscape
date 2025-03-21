package org.ironriders.elevator;

public class ElevatorConstants {

    public static final int PRIMARY_MOTOR_ID = 11;
    public static final int FOLLOW_MOTOR_ID = 10;

    public static final int ELEVATOR_MOTOR_STALL_LIMIT = 30; // in amps

    public static final double P = 0.3;
    public static final double I = 0.05;
    public static final double D = 0;

    public static final double MAX_VEL = 40;
    public static final double MAX_ACC = 40;
    public static final double ELEVATOR_POSITION_TOLERANCE = 0.15;

    public static final double T = .02;
    public static final double K_S = 0;
    public static final double K_G = .0765;
    public static final double K_V = 0;
    public static final double K_A = 0;

    public static final double GEAR_RATIO = 0.05;
    public static final double SPROCKET_DIAMETER = 1.7567;
    public static final int NUMBER_OF_STAGES = 2;
    public static final double INCHES_PER_ROTATION = GEAR_RATIO * SPROCKET_DIAMETER * Math.PI * NUMBER_OF_STAGES;

    public static final double DOWN_HEIGHT = 0;
    public static final double CORAL_STATION_HEIGHT = 8.8;
    public static final double ALGAE_PROCESSOR_HEIGHT = 0;
    public static final double LOW_ALGAE_HEIGHT = 21;
    public static final double HIGH_ALGAE_HEIGHT = 53;
    public static final double L1_HEIGHT = 4;
    public static final double L2_HEIGHT = 7.5;//untested
    public static final double L3_HEIGHT = 21; //tested (could be tested more)
    public static final double L4_HEIGHT = 53;//tested

    /**
     * This enum represents each specific/discrete position the elevator needs to go to.
     */
    public enum Level {
        Down(DOWN_HEIGHT),
        CoralStation(CORAL_STATION_HEIGHT),
        AlgaeProcessor(ALGAE_PROCESSOR_HEIGHT),
        LowAlgae(LOW_ALGAE_HEIGHT),
        HighAlgae(HIGH_ALGAE_HEIGHT),
        L1(L1_HEIGHT),
        L2(L2_HEIGHT),
        L3(L3_HEIGHT),
        L4(L4_HEIGHT);

        public double positionInches;

        private Level(double positionInches) {
            this.positionInches = positionInches;
        }
    }
}
