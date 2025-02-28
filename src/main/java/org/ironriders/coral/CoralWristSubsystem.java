package org.ironriders.coral;


import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static org.ironriders.coral.CoralWristConstants.*;

public class CoralWristSubsystem extends SubsystemBase {
    // Why do we extend subsystem base?
    // it publishes it to the command sceduler -tyler
    // no flirting in the comments, please -mischa
    private final CoralWristCommands commands;

    // find acutal motor IDs
    private final SparkMax motor = new SparkMax(CORALWRISTMOTOR, MotorType.kBrushless);
    private final PIDController pid = new PIDController(CORALWRISTKP, CORALWRISTKI, CORALWRISTKD);
    private  RelativeEncoder encoder = motor.getEncoder();
    private final SparkLimitSwitch forwardLimitSwitch = motor.getForwardLimitSwitch();
    private final SparkLimitSwitch reverseLimitSwitch = motor.getReverseLimitSwitch();
    private final LimitSwitchConfig forwardLimitSwitchConfig = new LimitSwitchConfig();
    private final LimitSwitchConfig reverseLimitSwitchConfig = new LimitSwitchConfig();
    private final SparkMaxConfig motorConfig = new SparkMaxConfig();
    private TrapezoidProfile.State goalState = new TrapezoidProfile.State();
    private TrapezoidProfile.State setPointState = new TrapezoidProfile.State();
    private final TrapezoidProfile profile;
    private boolean isHomed = false;

    // private ArmFeedforward coralFeedforward = new
    // ArmFeedforward(CORALWRISTKS,CORALWRISTKG,CORALWRISTKV);
    public CoralWristSubsystem() {
        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC);
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
                .idleMode(IdleMode.kBrake)
                .inverted(false)
                .apply(forwardLimitSwitchConfig)
                .apply(reverseLimitSwitchConfig);

        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        setGoal(getRotation());

        pid.setTolerance(CORAL_WRIST_TOLERANCE);

        commands = new CoralWristCommands(this);
    }

    @Override
    public void periodic() {
        setPointState = profile.calculate(t, setPointState, goalState);
        SmartDashboard.putNumber("Coral Wrist Set Postion", setPointState.position);
        double output = pid.calculate(getRotation(),setPointState.position);
        if(motor.getForwardLimitSwitch().isPressed()){
            handleTopLimitSwitch();
        }
        if(isHomed){
            motor.set(output);
            
        }
        
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "Homed", isHomed);
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "rotation", getRotation());
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "output", output);
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "setPoint", goalState.position);
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "forwardSwitch", motor.getForwardLimitSwitch().isPressed());
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "reverseSwitch", motor.getReverseLimitSwitch().isPressed());
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "motor current", motor.getOutputCurrent());
    }

    public void setGoal(double position) {
        System.out.println("Coral Wrist Set Position: " + position);
        goalState = new TrapezoidProfile.State(MathUtil.clamp(position, MIN_POSITION, MAX_POSITION), 0);
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
        return encoder.getPosition() * 360 * GEAR_RATIO;
    }

    public boolean atPosition() {
        return pid.atSetpoint();
    }

    public void homeCoralWrist(){
        boolean Bottomlimit = false;
        motor.set(.1);
        if(motor.getForwardLimitSwitch().isPressed()){
            motor.set(-0.05);
            Bottomlimit = true;
        }
        if(!motor.getForwardLimitSwitch().isPressed()&& Bottomlimit){
            encoder.setPosition(0);
        }
    }

    public CoralWristCommands getCommands() {
        return commands;
    }

}
