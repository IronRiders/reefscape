package org.ironriders.Manipulators.Coral;

import static org.ironriders.Manipulators.Coral.CoralWristConstants.CORAL_WRIST_CURRENT_STALL_LIMIT;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.CORAL_WRIST_MOTOR;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.CORAL_WRIST_TOLERANCE;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.D;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.ENCODER_OFFSET;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.FORWARD_LIMIT;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.GEAR_RATIO;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.I;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.MAX_ACC;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.MAX_VEL;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.P;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.REVERSE_LIMIT;
import static org.ironriders.Manipulators.Coral.CoralWristConstants.SPROCKET_RATIO;

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
