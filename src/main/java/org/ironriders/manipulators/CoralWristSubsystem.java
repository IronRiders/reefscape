package org.ironriders.manipulators;

import org.ironriders.core.Utils;

import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkBase.ResetMode;
import static org.ironriders.manipulators.ManipulatorConstants.*;

public class CoralWristSubsystem extends SubsystemBase {
    // Why do we extend subsystem base?
    // it publishes it to the command sceduler -tyler
    private final CoralWristCommands commands;

    // find acutal motor IDs
    private final SparkMax motor = new SparkMax(CORALWRISTMOTOR, MotorType.kBrushless);
    private final ProfiledPIDController pid = new ProfiledPIDController(0.1, 0, 0, PROFILE);
    private final DutyCycleEncoder absoluteEncoder = new DutyCycleEncoder(CORALWRISTENCODER);
    private final SparkLimitSwitch forwardLimitSwitch = motor.getForwardLimitSwitch();
    private final SparkLimitSwitch reverseLimitSwitch = motor.getReverseLimitSwitch();
    private final LimitSwitchConfig forwardLimitSwitchConfig = new LimitSwitchConfig();
    private final LimitSwitchConfig reverseLimitSwitchConfig = new LimitSwitchConfig();
    private final SparkMaxConfig motorConfig = new SparkMaxConfig();

    // private ArmFeedforward coralFeedforward = new
    // ArmFeedforward(CORALWRISTKS,CORALWRISTKG,CORALWRISTKV);
    public CoralWristSubsystem() {

        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true)
                .forwardLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // this sets allows the limit switch to
                                                                                 // disable the motor
        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true)
                .forwardLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // It also sets the Type to k normally
                                                                                 // closed see
                                                                                 // https://docs.revrobotics.com/brushless/spark-max/specs/data-port#limit-switch-operation
        motorConfig
                .smartCurrentLimit(CORAL_WRIST_CURRENT_STALL_LIMIT, CORAL_WRIST_CURRENT_FREE_LIMIT)
                .voltageCompensation(CORAL_WRIST_COMPENSATED_VOLTAGE)
                .idleMode(IdleMode.kBrake).limitSwitch
                .apply(forwardLimitSwitchConfig)
                .apply(reverseLimitSwitchConfig);
        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        set(getRotation());

        pid.setTolerance(Coral_Wrist_TOLERANCE);

        commands = new CoralWristCommands(this);
    }

    @Override
    public void periodic() {
        double output = pid.calculate(getRotation());
        motor.set(output);

        SmartDashboard.putNumber(DASHBOARD_PREFIX + "rotation", getRotation());
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "output", output);
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "setPoint", pid.getGoal().position);
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "fowardSwitch", forwardLimitSwitch.isPressed());
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "reverseSwitch", reverseLimitSwitch.isPressed());
    }

    public void set(double position) {
        pid.setGoal(position);
    }

    public void reset() {
        pid.setGoal(getRotation()); // Stops the wrist from moving
        pid.reset(getRotation()); // sets the error to zero but asssums it has no velocity
    }

    private double getRotation() {
        return Utils.absoluteRotation(absoluteEncoder.get() * 360 - CORAL_WRIST_ENCODER_OFFSET);
    }

    public boolean atPosition() {
        return pid.atGoal();
    }

    public CoralWristCommands getCommands() {
        return commands;
    }
}
