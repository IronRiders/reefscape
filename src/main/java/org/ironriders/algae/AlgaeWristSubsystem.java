package org.ironriders.algae;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import static org.ironriders.algae.AlgaeWristConstants.*;

import org.ironriders.lib.PID;
import org.ironriders.lib.WristSubsystem;

public class AlgaeWristSubsystem extends WristSubsystem {
    private final AlgaeWristCommands commands;

    public AlgaeWristSubsystem() {
        super(
            ALGAEWRISTMOTOR,
            GEAR_RATIO,
            HOME_ANGLE,
            true,
            new PID(AlgaeWristConstants.P, AlgaeWristConstants.I, AlgaeWristConstants.D),
            new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC),
            ALGAE_WRIST_CURRENT_STALL_LIMIT,
            false
        );

        commands = new AlgaeWristCommands(this);
    }

    public AlgaeWristCommands getCommands() {
        return commands;
    }
}
