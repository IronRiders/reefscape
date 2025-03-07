package org.ironriders.core;
public final class ControllerConstants {

    // Primary (Driver) Controller
    public static final int driveTranslateY = 0; // Left Stick Y-axis
    public static final int driveTranslateX = 1; // Left Stick X-axis
    public static final int driveRotateX = 4;    // Right Stick X-axis

    public static final int cancelPathfindX = 2; // X Button
    public static final int pathfindToTargetY = 3; // Y Button
    
    //public static final int scoreAlgae = 6; // Right Bumper
    //public static final int scoreCoral = 5; // Left Bumper



    public static final int jogControlDpad = 9; // D-Pad (Handled as POV)

    // Secondary (Operator) Controller

    
    public static final int scoreCoral = 0; // Trigger

    public static final int prepareGrabCoralTrigger = 1; // back hat button
    public static final int grabCoralReleaseTrigger = 1; // back hat button

    public static final int scoreAlgae = 1; // Y Axis?
    public static final int grabAlgaeTrigger = 1; // Y axis
    
    public static final int coralWristHome = 8;
    public static final int elevatorHome = 7;

    public static final int targetStation = 2;//pov left
    public static final int targetProcessor = 3;//pov righ

    public static final int climbUp = 13;
    public static final int climbStop = 14;
    public static final int climbDown = 15;

    public static final int setL4 = 9;
    public static final int setL3 = 7;
    public static final int setL2 = 5;
    public static final int setL1 = 4;

    public static final int targetReefAxis = 0; // X-Axis for reef targeting

    private ControllerConstants() {
        // Prevent instantiation
    }
}

    