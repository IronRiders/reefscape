package org.ironriders.wrist.algae;

import static org.ironriders.wrist.algae.AlgaeWristConstants.ALGAEWRISTMOTOR;
import static org.ironriders.wrist.algae.AlgaeWristConstants.ALGAE_WRIST_CURRENT_STALL_LIMIT;
import static org.ironriders.wrist.algae.AlgaeWristConstants.ENCODER_OFFSET;
import static org.ironriders.wrist.algae.AlgaeWristConstants.FORWARD_LIMIT;
import static org.ironriders.wrist.algae.AlgaeWristConstants.GEAR_RATIO;
import static org.ironriders.wrist.algae.AlgaeWristConstants.MAX_ACC;
import static org.ironriders.wrist.algae.AlgaeWristConstants.MAX_VEL;
import static org.ironriders.wrist.algae.AlgaeWristConstants.REVERSE_LIMIT;
import static org.ironriders.wrist.algae.AlgaeWristConstants.SPROCKET_RATIO;

import org.ironriders.lib.data.PID;
import org.ironriders.wrist.AbsoluteWristSubsystem;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class AlgaeWristSubsystem extends AbsoluteWristSubsystem {
    private final AlgaeWristCommands commands;

    public AlgaeWristSubsystem() {
        super(
                ALGAEWRISTMOTOR,
                GEAR_RATIO,
                SPROCKET_RATIO,
                ENCODER_OFFSET,
                REVERSE_LIMIT,
                FORWARD_LIMIT,
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
