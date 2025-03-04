package org.ironriders.lib;

import java.util.Optional;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;

/**
 * Functionality common to all wrists.
 * 
 * We interpret wrist angles in degrees forward relative to the floor; down is -90 and up is 90.  Angle must increase
 * as the motor moves forward.
 */
public abstract class WristSubsystem extends IronSubsystem {

    private final double HOMING_SETPOINT = .1;
    private final double HOMING_BACKOFF_SETPOINT = HOMING_SETPOINT / 2;
    private final double PERIOD = .02;

    private final SparkMax motor;
    protected final PIDController pid;
    private double gearRatio;

    private final RelativeEncoder encoder;
    private final SparkMaxConfig motorConfig = new SparkMaxConfig();

    private Optional<TrapezoidProfile.State> goalState = Optional.empty();
    private TrapezoidProfile.State setPointState = new TrapezoidProfile.State();
    private final TrapezoidProfile operationalProfile;
    private TrapezoidProfile movementProfile;

    private boolean isHomed = false;
    private final Angle homeAngle;
    private final boolean homeForward;
    private final SparkLimitSwitch reverseLimit;
    private final SparkLimitSwitch forwardLimit;
    private Optional<SparkLimitSwitch> goalLimit = Optional.empty();

    public WristSubsystem(
        int motorId,
        double gearRatio,
        Angle homeAngle,
        boolean homeForward,
        PID pid,
        TrapezoidProfile.Constraints constraints,
        int stallLimit,
        boolean inverted
    ) {
        motor = new SparkMax(motorId, MotorType.kBrushless);
        this.gearRatio = gearRatio;
        this.pid = new PIDController(pid.p, pid.i, pid.d);
        movementProfile = operationalProfile = new TrapezoidProfile(constraints);
        this.homeAngle = homeAngle;
        this.homeForward = homeForward;
        encoder = motor.getEncoder();

        var forwardLimitSwitchConfig = new LimitSwitchConfig();
        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true)
                .forwardLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // this sets allows the limit switch to
                                                                                 // disable the motor

        var reverseLimitSwitchConfig = new LimitSwitchConfig();
        reverseLimitSwitchConfig.reverseLimitSwitchEnabled(true)
                .reverseLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // It also sets the Type to k normally
                                                                                 // closed see
                                                                                 // https://docs.revrobotics.com/brushless/spark-max/specs/data-port#limit-switch-operation

        motorConfig
                .smartCurrentLimit(stallLimit)
                .idleMode(IdleMode.kBrake)
                .inverted(inverted)
                .apply(forwardLimitSwitchConfig)
                .apply(reverseLimitSwitchConfig);

        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        forwardLimit = motor.getForwardLimitSwitch();
        reverseLimit = motor.getReverseLimitSwitch();
    }

    @Override
    public void periodic() {
        double output = 0;
        double goal = Double.NaN;

        var currentDegrees = getCurrentAngle().in(Units.Degrees);

        if (this.goalState.isEmpty()) {
            motor.stopMotor();
        } else if (this.goalLimit.isPresent() && this.goalLimit.get().isPressed()) {
            this.reset();
        } else {
            setPointState = movementProfile.calculate(PERIOD, setPointState, goalState.get());
            output = pid.calculate(currentDegrees, setPointState.position);
            motor.set(output);
            goal = goalState.get().position;
        }
        
        publish("Homed", isHomed);
        publish("Rotation", currentDegrees);
        publish("Output", output);
        publish("Setpoint", motor.get());
        publish("Goal", goal);
        publish("ForwardLimit", forwardLimit.isPressed());
        publish("ReverseLimit", reverseLimit.isPressed());
        publish("Current", motor.getOutputCurrent());
    }

    public void setGoal(Angle angle) {
        var degrees = angle.in(Units.Degrees);

        if (!isHomed) {
            DriverStation.reportError("Blocking unhomed movement attempted for " + this.getClass().getSimpleName(), false); 
            return;
        }

        movementProfile = operationalProfile;
        goalState = Optional.of(new TrapezoidProfile.State(degrees, 0));

        var currentAngle = getCurrentAngle();
        if (currentAngle.equals(angle)) {
            return;
        }
        goalLimit = angle.equals(currentAngle)
            ? Optional.empty()
            : Optional.of(
                angle.gt(currentAngle)
                    ? forwardLimit
                    : reverseLimit
            );
    }

    public void reset() {
        goalState = Optional.empty();
        goalLimit = Optional.empty();
        setPointState.position = getCurrentAngle().in(Units.Degrees);
        setPointState.velocity = 0;
        pid.reset();
    }

    private Angle getCurrentAngle() {
        return Units.Degrees.of(encoder.getPosition() * 360 * gearRatio);
    }

    public boolean atPosition() {
        return pid.atSetpoint();
    }

    public Command moveToCmd(Angle angle) {
        return this.runOnce(() -> this.setGoal(angle));
    }

    public Command homeCmd() {
        return homeCmd(false);
    }

    public Command homeCmd(boolean force) {
        // If forced homing, rehome
        if (force) {
            isHomed = false;
        }

        // If homed, return to home position
        if (isHomed) {
            return Commands.runOnce(() -> {
                reset();
            });
            //return moveToCmd(homeAngle);
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
                    motor.set(HOMING_SETPOINT * direction);
                }

                public boolean isFinished() {
                    return limit.isPressed();
                }

                public void end(boolean interrupted) {
                    motor.stopMotor();
                }
            }
        );

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
            }
        );

        Command recordHome = this.runOnce(() -> {
            // Set encoder to rotations from 0 of home angle
            encoder.setPosition(homeAngle.in(Units.Degrees) / 360 / gearRatio);

            // Update setpoint to match current position
            this.setPointState.position = this.getCurrentAngle().in(Units.Degrees);
            this.goalState = Optional.empty();

            isHomed = true;
            this.reportInfo("Homed");
        });
    
        return findHome
            .andThen(moveOffHome)
            .andThen(recordHome);
    }
}
