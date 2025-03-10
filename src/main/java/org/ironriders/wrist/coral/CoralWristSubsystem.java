package org.ironriders.wrist.coral;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

import static org.ironriders.wrist.coral.CoralWristConstants.*;

import org.ironriders.lib.data.PID;
import org.ironriders.wrist.RelativeWristSubsystem;

public class CoralWristSubsystem extends RelativeWristSubsystem {
    private final CoralWristCommands commands;

    public CoralWristSubsystem() {
        super(
            CORALWRISTMOTOR,
            GEAR_RATIO,
            HOME_ANGLE,
            true,
            new PID(P, I, D),
            new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC),
            CORAL_WRIST_CURRENT_STALL_LIMIT,
            false
        );

        pid.setTolerance(CORAL_WRIST_TOLERANCE);

        commands = new CoralWristCommands(this);
    }

    public CoralWristCommands getCommands() {
        return commands;
    }
}
