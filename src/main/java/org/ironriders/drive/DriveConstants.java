package org.ironriders.drive;

import java.io.File;

import edu.wpi.first.wpilibj.Filesystem;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.config.PIDConstants;

public class DriveConstants {

    // Ports, IDs, Configs, etc.
    public static final int PRIMARY_CONTROLLER_PORT = 0;
    public static final int KEYPAD_CONTROLLER_PORT = 1;
    public static final int TERTIARY_CONTROLLER_PORT = 2;


    public static final File SWERVE_JSON_DIRECTORY = new File(Filesystem.getDeployDirectory(), "swerve");

    public static final PPHolonomicDriveController HOLONOMIC_CONFIG = new PPHolonomicDriveController(
            new PIDConstants(1.0, 0.0, 0.0), // Translation PID
            new PIDConstants(20.0, 0.0, 0.0) // Rotation PID
    );

    // Mathematical Constants
    public static final double TRANSLATION_CONTROL_EXPONENT = 3.0;
    public static final double TRANSLATION_CONTROL_DEADBAND = 0.8;
    public static final double ROTATION_CONTROL_EXPONENT = 3.0;
    public static final double ROTATION_CONTROL_DEADBAND = 0.8;

    public static final double SWERVE_DRIVE_MAX_SPEED = 1.0; // m/s

    public static final double SWERVE_MAXIMUM_SPEED_AUTO = .25; // m/s
    public static final double SWERVE_MAXIMUM_ACCELERATION_AUTO = SWERVE_MAXIMUM_SPEED_AUTO / 2; // m/s^2
    public static final double SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO = 10; // rad/s
    public static final double SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO = 5; // rad/s^2

    public static final double JOG_DISTANCE_INCHES = 1;
    public static final double JOG_SPEED = .25;

    public enum DriveSpeed {
        SLOW(0.5), NORMAL(1.0), FAST(1.5);

        public final double multiplier;

        DriveSpeed(double multiplier) {
            this.multiplier = multiplier;
        }
    }
}
