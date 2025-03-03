package org.ironriders.lib;

import java.util.Optional;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Functionality common to all wrists.
 * 
 * We interpret wrist angles in degrees forward relative to the floor; down is -90 and up is 90.
 * 
 * Wrist must move away from home when moving forward; invert if motor installed backwards.
 */
public abstract class WristSubsystem extends IronSubsystem {
    private final double HOMING_SETPOINT = .1;
    private final double HOMING_BACKOFF_SETPOINT = HOMING_SETPOINT / 2;
    private final double PERIOD = .02;

    private final SparkMax motor;
    protected final PIDController pid;
    protected boolean homeForward = false;
    private double gearRatio;
    private  RelativeEncoder encoder;
    private final SparkMaxConfig motorConfig = new SparkMaxConfig();
    private Optional<TrapezoidProfile.State> goalState = Optional.empty();
    private TrapezoidProfile.State setPointState = new TrapezoidProfile.State();
    private final TrapezoidProfile operationalProfile;
    private TrapezoidProfile movementProfile;
    private boolean isHomed = false;
    private final String diagnosticName = this.getClass().getSimpleName();
    private final String diagnosticPrefix = diagnosticName + "/";
    private final double homeAngle;
    private final boolean homeReverse;

    public WristSubsystem(
        int motorId,
        double gearRatio,
        double homeAngle,
        boolean homeReverse,
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
        this.homeReverse = homeReverse;
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
    }

    @Override
    public void periodic() {
        double output;
        double goal;

        if (this.goalState.isEmpty()) {
            output = 0;
            goal = -1;
            motor.set(0);
        } else {
            setPointState = movementProfile.calculate(PERIOD, setPointState, goalState.get());
            output = pid.calculate(getRotation(), setPointState.position);
            motor.set(output);
            goal = goalState.get().position;
        }
        
        addDiagnostic("Homed", isHomed);
        addDiagnostic("Rotation", getRotation());
        addDiagnostic("Output", output);
        addDiagnostic("Setpoint", motor.get());
        addDiagnostic("Goal", goal);
        addDiagnostic("ForwardLimit", motor.getForwardLimitSwitch().isPressed());
        addDiagnostic("ReverseLimit", motor.getReverseLimitSwitch().isPressed());
        addDiagnostic("Current", motor.getOutputCurrent());
    }

    public void setGoal(double degrees) {
        if (!isHomed) {
            DriverStation.reportError("Blocking unhomed movement attempted for " + this.getClass().getSimpleName(), false); 
            return;
        }

        movementProfile = operationalProfile;
        goalState = Optional.of(new TrapezoidProfile.State(degrees - homeAngle, 0));
    }

    public void reset() {
        goalState = Optional.empty();
        pid.reset();
    }

    private double getRotation() {
        return homeAngle + encoder.getPosition() * 360 * gearRatio * (homeForward ? 1 : -1);
    }

    public boolean atPosition() {
        return pid.atSetpoint();
    }

    public Command moveToCmd(double position) {
        return this
            .runOnce(() -> this.setGoal(position))
            .until(this::atPosition)
            .handleInterrupt(this::reset);
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
            return moveToCmd(0);
        }

        var limit = homeReverse
            ? motor.getReverseLimitSwitch()
            : motor.getForwardLimitSwitch();

        System.out.println("Homing " + this.diagnosticName);

        var direction = homeReverse ? -1 : 1;

        Command findHome = this.defer(
            () -> new Command() {
                public void execute() {
                    motor.set(HOMING_SETPOINT * direction);
                }

                public boolean isFinished() {
                    return limit.isPressed();
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
            }
        );

        Command recordHome = this.runOnce(() -> {
            encoder.setPosition(0);
            isHomed = true;
            this.setPointState.position = 0;
            System.out.println("Homed" + this.diagnosticName);
        });
    
        return findHome
            .andThen(moveOffHome)
            .andThen(recordHome);
    }
}
