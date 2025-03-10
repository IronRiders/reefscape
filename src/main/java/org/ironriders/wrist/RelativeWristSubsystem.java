package org.ironriders.wrist;

import org.ironriders.lib.data.PID;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.config.LimitSwitchConfig;

import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;

/**
 * Functionality common to wrists without an absolute encoder.
 */
public abstract class RelativeWristSubsystem extends WristSubsystem {

    private final double HOMING_SPEED = .1;
    private final double HOMING_BACKOFF_SETPOINT = HOMING_SPEED / 2;
    private final Angle CRASH_BACKOFF = Units.Degrees.of(1);

    private final RelativeEncoder encoder;

    private boolean isHomed = false;
    private final Angle homeAngle;
    private final boolean homeForward;
    private final SparkLimitSwitch reverseLimit;
    private final SparkLimitSwitch forwardLimit;

    public RelativeWristSubsystem(
            int motorId,
            double gearRatio,
            Angle homeAngle,
            boolean homeForward,
            PID pid,
            TrapezoidProfile.Constraints constraints,
            int stallLimit,
            boolean inverted) {
        super(motorId, gearRatio, pid, constraints, stallLimit, inverted);

        this.homeAngle = homeAngle;
        this.homeForward = homeForward;
        encoder = motor.getEncoder();

        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        forwardLimit = motor.getForwardLimitSwitch();
        reverseLimit = motor.getReverseLimitSwitch();
    }

    @Override
    protected void configureMotor() {
        var forwardLimitSwitchConfig = new LimitSwitchConfig();
        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true)
                .forwardLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // this sets allows the limit switch to
                                                                                 // disable the motor

        var reverseLimitSwitchConfig = new LimitSwitchConfig();
        reverseLimitSwitchConfig.reverseLimitSwitchEnabled(true)
                .reverseLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // It also sets the Type to k normally
                                                                                 // closed see
                                                                                 // https://docs.revrobotics.com/brushless/spark-max/specs/data-port#limit-switch-operation

        motorConfig.apply(forwardLimitSwitchConfig).apply(reverseLimitSwitchConfig);

        super.configureMotor();
    }

    @Override
    boolean isHomed() {
        return isHomed;
    }

    @Override
    protected boolean isAtForwardLimit() {
        return forwardLimit.isPressed();
    }

    @Override
    protected boolean isAtReverseLimit() {
        return reverseLimit.isPressed();
    }

    /**
     * Update the periodic setpoint and set the motor level.
     */
    @Override
    protected void setMotorLevel() {
        if (!isHomed) {
            return;
        }

        // If we hit a limit, move off a bit so we can try to keep motor
        // engaged without continually bouncing. This will be iterative so
        // the more we bounce, the more we back off. Only back off if the
        // backoff is past the goal because large bounces may push us further
        // out of bounds
        if (this.forwardLimit.isPressed()) {
            reportWarning("Crashed on forward limit");
            var backoffTo = getCurrentAngle().minus(CRASH_BACKOFF);
            if (backoffTo.lt(Units.Degrees.of(goalSetpoint.position))) {
                this.setGoal(backoffTo);
            }
        } else if (this.reverseLimit.isPressed()) {
            reportWarning("Crashed on reverse limit");
            var backoffTo = getCurrentAngle().plus(CRASH_BACKOFF);
            if (backoffTo.gt(Units.Degrees.of(goalSetpoint.position))) {
                this.setGoal(backoffTo);
            }
        }

        super.setMotorLevel();
    }

    @Override
    protected Angle getCurrentAngle() {
        return Units.Degrees.of(encoder.getPosition() * 360 * gearRatio);
    }

    public Command homeCmd(boolean force) {
        // If forced homing, rehome
        if (force) {
            isHomed = false;
        }

        // If homed, leave wrist as is but update internal state
        if (isHomed) {
            return Commands.runOnce(this::reset);
        }

        SparkLimitSwitch limit;
        double direction;
        if (homeForward) {
            limit = forwardLimit;
            direction = 1;
        } else {
            limit = reverseLimit;
            direction = -1;
        }

        this.reportInfo("Homing");

        Command findHome = this.defer(
                () -> new Command() {
                    public void execute() {
                        motor.set(HOMING_SPEED * direction);
                    }

                    public boolean isFinished() {
                        return limit.isPressed();
                    }

                    public void end(boolean interrupted) {
                        motor.stopMotor();
                    }
                });

        Command moveOffHome = this.defer(
                () -> new Command() {
                    public void execute() {
                        motor.set(HOMING_BACKOFF_SETPOINT * -direction);
                    }

                    public boolean isFinished() {
                        return !limit.isPressed();
                    }

                    public void end(boolean interrupted) {
                        motor.stopMotor();
                    }
                });

        Command recordHome = this.runOnce(() -> {
            // Set encoder to rotations from 0 of home angle
            encoder.setPosition(homeAngle.in(Units.Degrees) / 360 / gearRatio);

            // Update setpoint to match current position
            reset();

            isHomed = true;
            this.reportInfo("Homed");
        });

        Command waitForHome = Commands.waitUntil(this::isHomed);

        return findHome
                .andThen(moveOffHome)
                .andThen(recordHome)
                .andThen(waitForHome);
    }
}
