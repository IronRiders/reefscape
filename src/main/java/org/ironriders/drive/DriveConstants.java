package org.ironriders.drive;

import java.io.File;

import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.wpilibj.Filesystem;

public class DriveConstants {

    public static final double ROBOT_WIDTH = 37; // Front-back
    public static final double ROBOT_LENGTH = 37; // Side-side

    // Ports, IDs, Configs, etc.
    public static final int PRIMARY_CONTROLLER_PORT = 0;
    public static final int KEYPAD_CONTROLLER_PORT = 1;
    public static final int TERTIARY_CONTROLLER_PORT = 2;
    public static boolean INVERTTELEOPCONTROLS = false;

    public static final double PATHFIND_CANCEL_THRESHOLD = 0.3; // 0-1, controller input

    public static final File SWERVE_JSON_DIRECTORY = new File(Filesystem.getDeployDirectory(), "swerve");

	public static final PathConstraints PATH_CONSTRAINTS = new PathConstraints(
		DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO,
		DriveConstants.SWERVE_MAXIMUM_ACCELERATION_AUTO,
		DriveConstants.SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO,
		DriveConstants.SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO);

    // Mathematical Constants
    public static final double TRANSLATION_CONTROL_EXPONENT = 3.0;
    public static final double TRANSLATION_CONTROL_DEADBAND = 0.8;
    public static final double ROTATION_CONTROL_EXPONENT = 3.0;
    public static final double ROTATION_CONTROL_DEADBAND = 0.8;

    public static final double SWERVE_DRIVE_MAX_SPEED = 5.0; // m/s
    public static final double SWERVE_MAXIMUM_ANGULAR_VELOCITY_TELEOP = Math.PI * 4; // rad/s

    public static final double SWERVE_MAXIMUM_SPEED_AUTO = 4; // m/s
    public static final double SWERVE_MAXIMUM_ACCELERATION_AUTO = SWERVE_MAXIMUM_SPEED_AUTO / 2; // m/s^2
    public static final double SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO = Math.PI * 4; // rad/s
    public static final double SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO = SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO / 2; // rad/s^2

    public static final double AUTO_TOLERANCE_POSITION = 0.1; // meters
    public static final double AUTO_TOLERANCE_ROTATION = Math.toDegrees(1); // degrees

    public static final double JOG_DISTANCE_INCHES = 1;
    public static final double JOG_SPEED = .25;
}
