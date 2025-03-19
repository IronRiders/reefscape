package org.ironriders.Manipulators.Coral.CoralWrist;

import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.CORAL_WRIST_CURRENT_STALL_LIMIT;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.CORAL_WRIST_MOTOR;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.CORAL_WRIST_TOLERANCE;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.D;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.ENCODER_OFFSET;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.FORWARD_LIMIT;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.GEAR_RATIO;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.I;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.MAX_ACC;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.MAX_VEL;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.P;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.REVERSE_LIMIT;
import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.SPROCKET_RATIO;

import org.ironriders.Manipulators.AbsoluteWristSubsystem;
import org.ironriders.Manipulators.AbsoluteWristSubsystemCoral;
import org.ironriders.lib.data.PID;

import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class CoralWristSubsystem extends AbsoluteWristSubsystemCoral {
    private final CoralWristCommands commands;

    public CoralWristSubsystem() {
        super(
                CORAL_WRIST_MOTOR,
                GEAR_RATIO,
                SPROCKET_RATIO,
                ENCODER_OFFSET,
                REVERSE_LIMIT,
                FORWARD_LIMIT,
                false,
                new PID(P, I, D),
                new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC),
                CORAL_WRIST_CURRENT_STALL_LIMIT,
                false);

        pid.setTolerance(CORAL_WRIST_TOLERANCE);

        commands = new CoralWristCommands(this);
    }

    public CoralWristCommands getCommands() {
        return commands;
    }
}
