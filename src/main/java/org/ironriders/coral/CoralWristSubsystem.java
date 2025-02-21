package org.ironriders.coral;

import org.ironriders.core.Utils;
import org.ironriders.elevator.ElevatorConstants;

import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static org.ironriders.coral.CoralWristConstants.*;
import static org.ironriders.elevator.ElevatorConstants.D;
import static org.ironriders.elevator.ElevatorConstants.I;
import static org.ironriders.elevator.ElevatorConstants.P;

public class CoralWristSubsystem extends SubsystemBase {
    // Why do we extend subsystem base?
    // it publishes it to the command sceduler -tyler
    private final CoralWristCommands commands;

    // find acutal motor IDs
    private final SparkMax motor = new SparkMax(CORALWRISTMOTOR, MotorType.kBrushless);
    private final PIDController pid = new PIDController(P, I, D);
    private final DutyCycleEncoder absoluteEncoder = new DutyCycleEncoder(CORALWRISTENCODER);
    private final SparkLimitSwitch forwardLimitSwitch = motor.getForwardLimitSwitch();
    private final SparkLimitSwitch reverseLimitSwitch = motor.getReverseLimitSwitch();
    private final LimitSwitchConfig forwardLimitSwitchConfig = new LimitSwitchConfig();
    private final LimitSwitchConfig reverseLimitSwitchConfig = new LimitSwitchConfig();
    private final SparkMaxConfig motorConfig = new SparkMaxConfig();
    private TrapezoidProfile.State goalState = new TrapezoidProfile.State();
    private TrapezoidProfile.State setPointState = new TrapezoidProfile.State();
    private final TrapezoidProfile profile;

    // private ArmFeedforward coralFeedforward = new
    // ArmFeedforward(CORALWRISTKS,CORALWRISTKG,CORALWRISTKV);
    public CoralWristSubsystem() {
        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(ElevatorConstants.MAX_VEL, ElevatorConstants.MAX_ACC);
        profile = new TrapezoidProfile(constraints);

        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true)
                .forwardLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // this sets allows the limit switch to
                                                                                 // disable the motor
        reverseLimitSwitchConfig.reverseLimitSwitchEnabled(true)
                .reverseLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // It also sets the Type to k normally
                                                                                 // closed see
                                                                                 // https://docs.revrobotics.com/brushless/spark-max/specs/data-port#limit-switch-operation
        motorConfig
                .smartCurrentLimit(CORAL_WRIST_CURRENT_STALL_LIMIT)
                // .voltageCompensation(CORAL_WRIST_COMPENSATED_VOLTAGE)
                .idleMode(IdleMode.kBrake)
                .apply(forwardLimitSwitchConfig)
                .apply(reverseLimitSwitchConfig);

        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        setGoal(getRotation());

        pid.setTolerance(CORAL_WRIST_TOLERANCE);

        commands = new CoralWristCommands(this);
    }

    @Override
    public void periodic() {
        setPointState = profile.calculate(ElevatorConstants.T, setPointState, goalState);
        SmartDashboard.putNumber("Coral Wrist Set Postion", setPointState.position);
        double output = pid.calculate(getRotation(),setPointState.position);
        motor.set(output);

        SmartDashboard.putNumber(DASHBOARD_PREFIX + "rotation", getRotation());
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "output", output);
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "setPoint", goalState.position);
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "fowardSwitch", forwardLimitSwitch.isPressed());
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "reverseSwitch", reverseLimitSwitch.isPressed());
    }

    public void setGoal(double position) {
        goalState = new TrapezoidProfile.State(position, 0);
    }

    public void reset() {
        goalState = new TrapezoidProfile.State(0, 0);
        
    }

    private double getRotation() {
        return Utils.absoluteRotation(absoluteEncoder.get() * 360 - CORAL_WRIST_ENCODER_OFFSET);
    }

    public boolean atPosition() {
        return pid.atSetpoint();
    }

    public CoralWristCommands getCommands() {
        return commands;
    }

}
