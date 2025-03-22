package org.ironriders.wrist;

import org.ironriders.lib.IronSubsystem;
import org.ironriders.lib.data.PID;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

/**
 * Functionality common to all wrists.
 * 
 * We interpret wrist angles in degrees forward relative to the floor; down is
 * -90 and up is 90. Angle must increase as the motor moves forward.
 */
public abstract class WristSubsystem extends IronSubsystem {

    private final double PERIOD = .02;

    protected final SparkMax motor;
    protected final PIDController pid;
    protected final double gearRatio;

    protected final SparkMaxConfig motorConfig = new SparkMaxConfig();

    protected TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State();
    private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State();
    private final TrapezoidProfile movementProfile;

    abstract boolean isHomed();

    protected abstract boolean isAtForwardLimit();

    protected abstract boolean isAtReverseLimit();

    protected abstract Angle getCurrentAngle();

    public abstract Command homeCmd(boolean force);

    protected WristSubsystem(
            int motorId,
            double gearRatio,
            PID pid,
            TrapezoidProfile.Constraints constraints,
            int stallLimit,
            boolean inverted) {
        motor = new SparkMax(motorId, MotorType.kBrushless);
        this.gearRatio = gearRatio;
        this.pid = new PIDController(pid.p, pid.i, pid.d);
        movementProfile = new TrapezoidProfile(constraints);

        motorConfig
                .smartCurrentLimit(stallLimit)
                .idleMode(IdleMode.kBrake)
                .inverted(inverted);
        configureMotor();
    }

    protected void configureMotor() {
        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    @Override
    public void periodic() {
        setMotorLevel();

        publish("Homed", isHomed());
        publish("Rotation", getCurrentAngle().in(Units.Degrees));
        publish("Output", motor.get());
        publish("Goal", goalSetpoint.position);
        publish("Current", motor.getOutputCurrent());
        publish("ForwardLimit", isAtForwardLimit());
        publish("ReverseLimit", isAtReverseLimit());
    }

    /**
     * Update the periodic setpoint and set the motor level.
     */
    protected void setMotorLevel() {
        var currentDegrees = getCurrentAngle().in(Units.Degrees);

        // Apply profile and PID to determine output level
        periodicSetpoint = movementProfile.calculate(PERIOD, periodicSetpoint, goalSetpoint);
        var speed = pid.calculate(currentDegrees, periodicSetpoint.position);
        motor.set(speed);
    }

    public void setGoal(Angle angle) {
        var degrees = angle.in(Units.Degrees);

        if (!isHomed()) {
            DriverStation.reportError("Blocking unhomed movement attempted for " + this.getClass().getSimpleName(),
                    false);
            return;
        }

        goalSetpoint = new TrapezoidProfile.State(degrees, 0);

        var currentAngle = getCurrentAngle();
        if (currentAngle.equals(angle)) {
            return;
        }
    }

    public void reset() {
        var currentAngle = getCurrentAngle();

        goalSetpoint = createSetpoint(currentAngle);
        periodicSetpoint = createSetpoint(currentAngle);

        pid.reset();
    }

    private TrapezoidProfile.State createSetpoint(Angle angle) {
        return createSetpoint(angle, 0);
    }

    private TrapezoidProfile.State createSetpoint(Angle angle, double velocity) {
        return new TrapezoidProfile.State(angle.in(Units.Degrees), velocity);
    }

    public boolean atPosition() {
        return Math.abs(getCurrentAngle().in(Units.Degrees) - goalSetpoint.position) < 2;
    }

    public Command moveToCmd(Angle angle) {
        return this.runOnce(() -> this.setGoal(angle)).andThen(Commands.waitUntil(this::atPosition));
    }

    public Command homeCmd() {
        return homeCmd(false);
    }
}
