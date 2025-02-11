package org.ironriders.drive;

import java.io.File;
import edu.wpi.first.wpilibj.Filesystem;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.config.PIDConstants;

public class DriveConstants {

    // Ports, IDs, Configs, etc.
    public static final int PRIMARY_CONTROLLER_PORT = 0;

    public static final File SWERVE_JSON_DIRECTORY = new File(Filesystem.getDeployDirectory(), "swerve");
    public static final PPHolonomicDriveController HOLONOMIC_CONFIG = new PPHolonomicDriveController( // PPHolonomicController
                                                                                                      // is the built in
                                                                                                      // path following
                                                                                                      // controller for
                                                                                                      // holonomic drive
                                                                                                      // trains
            new PIDConstants(5.0, 0.0, 0.0), // Translation PID
            new PIDConstants(5.0, 0.0, 0.0) // Rotation PID
    );

    // Mathematical Constants
    public static final double TRANSLATION_CONTROL_EXPONENT = 3.0;
    public static final double TRANSLATION_CONTROL_DEADBAND = 0.8;
    public static final double ROTATION_CONTROL_EXPONENT = 3.0;
    public static final double ROTATION_CONTROL_DEADBAND = 0.8;
    public static final double SWERVE_MAXIMUM_SPEED_TELEOP = .5; // m/s
    public static final double SWERVE_MAXIMUM_SPEED_AUTO = .25; // m/s these can probably be recombined for comp but
                                                                // for testing the distinction matters
}
