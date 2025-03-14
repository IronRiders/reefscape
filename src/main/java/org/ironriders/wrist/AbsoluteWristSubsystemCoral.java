package org.ironriders.wrist;

import org.ironriders.lib.data.PID;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SoftLimitConfig;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Command;

public class AbsoluteWristSubsystemCoral extends WristSubsystem {

    private final boolean invertEncoder;
    private final Angle encoderOffset;
    private final double encoderScale;
    private final Angle reverseLimit;
    private final Angle forwardLimit;
    private boolean hasGoal = false;

    AbsoluteEncoder encoder;

    public AbsoluteWristSubsystemCoral(
            int motorId,
            double gearRatio,
            double encoderScale,
            Angle encoderOffset,
            Angle reverseLimit,
            Angle forwardLimit,
            boolean invertEncoder,
            PID pid,
            TrapezoidProfile.Constraints constraints,
            int stallLimit,
            boolean inverted) {
        super(motorId, gearRatio, pid, constraints, stallLimit, inverted);

        this.encoderScale = encoderScale;
        this.encoderOffset = encoderOffset;
        this.reverseLimit = reverseLimit;
        this.forwardLimit = forwardLimit;
        this.invertEncoder = invertEncoder;

        encoder = motor.getAbsoluteEncoder();
        encoder.getPosition();

        reset();
    }

    @Override
    protected boolean isAtForwardLimit() {
        return getCurrentAngle().gt(forwardLimit);
    }

    @Override
    protected boolean isAtReverseLimit() {
        return getCurrentAngle().lt(reverseLimit);
    }

   /*  @Override
    protected void configureMotor() {
        var softLimitConfig = new SoftLimitConfig();

        // I *think* soft limits are set in rotations of the internal encoder,
        // not the absolute encoder. This isn't documented. However, on current
        // bot, disabling limit is worse than crashing because current is low and
        // if the motor disables the arm will crash into bearing brackets on
        // elevator. So leaving disabled for now
        softLimitConfig
                .reverseSoftLimitEnabled(false)
                .forwardSoftLimitEnabled(false);
        // .reverseSoftLimitEnabled(true)
        // .reverseSoftLimit(TODO)
        // .forwardSoftLimitEnabled(true)
        // .forwardSoftLimit(TODO);

        var limitSwitchConfig = new LimitSwitchConfig();
        limitSwitchConfig
                .reverseLimitSwitchEnabled(false)
                .forwardLimitSwitchEnabled(false);

      //  motorConfig
        //        .apply(softLimitConfig);

        super.configureMotor();
    }*/

    @Override
    protected void setMotorLevel() {
        // Leave motor idling in break mode until we are told to go somewhere
        if (!hasGoal) {
            return;
        }

        super.setMotorLevel();
    }

    @Override
    public void setGoal(Angle goal) {
        if (goal.lt(reverseLimit)) {
            goal = reverseLimit;
        }

        if (goal.gt(forwardLimit)) {
            goal = forwardLimit;
        }

        super.setGoal(goal);

        hasGoal = true;
    }

    @Override
    protected boolean isHomed() {
        return true;
    }

    @Override
    protected Angle getCurrentAngle() {
        var angle = Units.Rotations.of(encoder.getPosition());
        publish("rawRotation",Units.Rotations.of(encoder.getPosition()).in(Units.Degrees) );
        angle = angle.times(encoderScale);

        if (invertEncoder) {
            angle = angle.times(-1);
        }

        angle = angle.plus(encoderOffset);

        return angle;
    }

    @Override
    public Command homeCmd(boolean force) {
        return this.runOnce(this::reset);
    }
}
