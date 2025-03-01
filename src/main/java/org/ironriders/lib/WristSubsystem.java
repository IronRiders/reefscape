package org.ironriders.lib;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Functionality common to all wrists.
 */
public abstract class WristSubsystem extends SubsystemBase {
    private final SparkMax motor;
    protected final PIDController pid;
    protected double t = 0.2;
    protected double homeSpeed = -.1;
    private double gearRatio;
    private  RelativeEncoder encoder;
    private final SparkMaxConfig motorConfig = new SparkMaxConfig();
    private TrapezoidProfile.State goalState = new TrapezoidProfile.State();
    private TrapezoidProfile.State setPointState = new TrapezoidProfile.State();
    private final TrapezoidProfile profile;
    private boolean isHomed = false;
    private String diagnosticPrefix = this.getClass().getName() + "/";
    private Range<Double> range;

    public WristSubsystem(int motorId, double gearRatio, Range<Double> range, PID pid, TrapezoidProfile.Constraints constraints, int stallLimit) {
        motor = new SparkMax(motorId, MotorType.kBrushless);
        this.gearRatio = gearRatio;
        this.pid = new PIDController(pid.p, pid.i, pid.d);
        profile = new TrapezoidProfile(constraints);
        this.range = range;
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
                .inverted(false)
                .apply(forwardLimitSwitchConfig)
                .apply(reverseLimitSwitchConfig);

        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        setGoal(getRotation());
    }

    @Override
    public void periodic() {
        setPointState = profile.calculate(t, setPointState, goalState);
        double output = pid.calculate(getRotation(),setPointState.position);
        if(motor.getForwardLimitSwitch().isPressed()){
            handleTopLimitSwitch();
        }
        if(isHomed){
            motor.set(output);
            
        }
        
        SmartDashboard.putBoolean(diagnosticPrefix + "homed", isHomed);
        SmartDashboard.putNumber(diagnosticPrefix + "rotation", getRotation());
        SmartDashboard.putNumber(diagnosticPrefix + "output", output);
        SmartDashboard.putNumber(diagnosticPrefix + "setpoint", setPointState.position);
        SmartDashboard.putNumber(diagnosticPrefix + "goal", goalState.position);
        SmartDashboard.putBoolean(diagnosticPrefix + "forwardLimit", motor.getForwardLimitSwitch().isPressed());
        SmartDashboard.putBoolean(diagnosticPrefix + "reverseLimit", motor.getReverseLimitSwitch().isPressed());
        SmartDashboard.putNumber(diagnosticPrefix + "current", motor.getOutputCurrent());
    }

    public void setGoal(double position) {
        System.out.println("Coral Wrist Set Position: " + position);
        goalState = new TrapezoidProfile.State(MathUtil.clamp(position, this.range.low, this.range.high), 0);
    }

    public void reset() {
        goalState = new TrapezoidProfile.State(setPointState.position, 0);
        pid.reset();
        
    }
    private void handleTopLimitSwitch() {
        encoder.setPosition(0);
        isHomed = true;
    }


    private double getRotation() {
        return encoder.getPosition() * 360 * gearRatio;
    }

    public boolean atPosition() {
        return pid.atSetpoint();
    }

    public Command homeCmd() {
        return homeCmd(false);
    }

    public Command homeCmd(boolean force) {
        if (force) {
            isHomed = false;
        }
        var limit = homeSpeed < 0 ? motor.getReverseLimitSwitch() : motor.getForwardLimitSwitch();
        return this
            .run(() -> motor.set(this.homeSpeed))
            .until(() -> isHomed || limit.isPressed())
            .andThen(this
                .run(() -> motor.set(-this.homeSpeed))
                .until(() -> isHomed || !limit.isPressed())
                .andThen(this.run(() -> {
                    isHomed = true;
                    encoder.setPosition(0);
                }))
            );
    }
}
