package org.ironriders.coral;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import static org.ironriders.coral.CoralWristConstants.*;

import org.ironriders.lib.PID;
import org.ironriders.lib.Range;
import org.ironriders.lib.WristSubsystem;

public class CoralWristSubsystem extends WristSubsystem {
    private final CoralWristCommands commands;

    // private ArmFeedforward coralFeedforward = new
    // ArmFeedforward(CORALWRISTKS,CORALWRISTKG,CORALWRISTKV);
    public CoralWristSubsystem() {
        super(
            CORALWRISTMOTOR,
            GEAR_RATIO,
            new Range<Double>(MIN_POSITION, MAX_POSITION),
            new PID(CORALWRISTKP, CORALWRISTKI, CORALWRISTKD),
            new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC),
            CORAL_WRIST_CURRENT_STALL_LIMIT
        );

        pid.setTolerance(CORAL_WRIST_TOLERANCE);
        homeSpeed = .1;

        commands = new CoralWristCommands(this);
    }

    public CoralWristCommands getCommands() {
        return commands;
    }
}
