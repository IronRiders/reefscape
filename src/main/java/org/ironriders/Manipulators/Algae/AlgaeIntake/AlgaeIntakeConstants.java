package org.ironriders.Manipulators.Algae.AlgaeIntake;

import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import org.ironriders.Drive.DriveConstants;

public final class AlgaeIntakeConstants {
    public static final int ALGAE_LEFT_ID = 15;
    public static final int ALGAE_RIGHT_ID = 16;

    public static final int ALGAE_INTAKE_CURRENT_STALL_LIMIT = 2; // FOR TESTING!!!

    public static final int EJECT_TIMEOUT = 1; // seconds
    public static final int INTAKE_TIMEOUT = 1; // seconds
    public static final double MAX_SPEED_COEFFICIENT = 0.8; // Scale joystick input

    private AlgaeIntakeConstants() {
        // Prevent instantiation of this constants class
    }
}
