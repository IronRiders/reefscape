package org.ironriders.algae;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import static org.ironriders.algae.AlgaeWristConstants.*;

import org.ironriders.lib.PID;
import org.ironriders.lib.Range;
import org.ironriders.lib.WristSubsystem;

public class AlgaeWristSubsystem extends WristSubsystem {
    // Why do we extend subsystem base?
    private final AlgaeWristCommands commands;

    public AlgaeWristSubsystem() {
        super(
            ALGAEWRISTMOTOR,
            GEAR_RATIO,
            new Range<Double>(MIN_POSITION, MAX_POSITION),
            new PID(AlgaeWristConstants.P, AlgaeWristConstants.I, AlgaeWristConstants.D),
            new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC),
            ALGAE_WRIST_CURRENT_STALL_LIMIT
        );

        commands = new AlgaeWristCommands(this);
    }

    public AlgaeWristCommands getCommands() {
        return commands;
    }
}
