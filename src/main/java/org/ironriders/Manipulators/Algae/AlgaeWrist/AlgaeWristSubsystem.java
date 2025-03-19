package org.ironriders.Manipulators.Algae.AlgaeWrist;

import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.ALGAE_WRIST_CURRENT_STALL_LIMIT;
import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.ALGAE_WRIST_MOTOR;
import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.ENCODER_OFFSET;
import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.FORWARD_LIMIT;
import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.GEAR_RATIO;
import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.MAX_ACC;
import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.MAX_VEL;
import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.REVERSE_LIMIT;
import static org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants.SPROCKET_RATIO;

import org.ironriders.Manipulators.AbsoluteWristSubsystem;
import org.ironriders.lib.data.PID;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class AlgaeWristSubsystem extends AbsoluteWristSubsystem {
    private final AlgaeWristCommands commands;

    public AlgaeWristSubsystem() {
        super(
                ALGAE_WRIST_MOTOR,
                GEAR_RATIO,
                SPROCKET_RATIO,
                ENCODER_OFFSET,
                REVERSE_LIMIT,
                FORWARD_LIMIT,
                true,
                new PID(AlgaeWristConstants.P, AlgaeWristConstants.I, AlgaeWristConstants.D),
                new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC),
                ALGAE_WRIST_CURRENT_STALL_LIMIT,
                false);

        commands = new AlgaeWristCommands(this);
    }

    public AlgaeWristCommands getCommands() {
        return commands;
    }
}
