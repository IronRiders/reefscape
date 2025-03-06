package org.ironriders.wrist.coral;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

import static org.ironriders.wrist.coral.CoralWristConstants.*;

import org.ironriders.lib.data.PID;
import org.ironriders.wrist.WristSubsystem;

public class CoralWristSubsystem extends WristSubsystem {
    private final CoralWristCommands commands;

    // private ArmFeedforward coralFeedforward = new
    // ArmFeedforward(CORALWRISTKS,CORALWRISTKG,CORALWRISTKV);
    public CoralWristSubsystem() {
        super(
            CORALWRISTMOTOR,
            GEAR_RATIO,
            HOME_ANGLE,
            true,
            new PID(CORALWRISTKP, CORALWRISTKI, CORALWRISTKD),
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
