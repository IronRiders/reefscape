package org.ironriders.algae;

import org.ironriders.core.Utils;

import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static org.ironriders.algae.AlgaeWristConstants.*;

import org.ironriders.algae.AlgaeWristCommands;


public class AlgaeWristSubsystem extends SubsystemBase {
    // Why do we extend subsystem base?
    private final AlgaeWristCommands commands;

    // find acutal motor IDs
    private final SparkMax motor = new SparkMax(ALGAEWRISTMOTOR, MotorType.kBrushless);
    private final ProfiledPIDController pid = new ProfiledPIDController(ALGAEWRISTKP, ALGAEWRISTKI, ALGAEWRISTKD, PROFILE);
    private final DutyCycleEncoder absoluteEncoder = new DutyCycleEncoder(ALGAEWRISTENCODER);
    private final SparkLimitSwitch forwardLimitSwitch = motor.getForwardLimitSwitch();
    private final SparkLimitSwitch reverseLimitSwitch = motor.getReverseLimitSwitch();
    private final LimitSwitchConfig forwardLimitSwitchConfig = new LimitSwitchConfig();
    private final LimitSwitchConfig reverseLimitSwitchConfig = new LimitSwitchConfig();
    private final SparkMaxConfig motorConfig = new SparkMaxConfig();

    public AlgaeWristSubsystem() {

        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true)
                .forwardLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // this sets allows the limit switch to
                                                                                 // disable the motor
        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true)
                .forwardLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // It also sets the Type to k normally
                                                                                 // closed see
                                                                                 // https://docs.revrobotics.com/brushless/spark-max/specs/data-port#limit-switch-operation
        motorConfig
            .smartCurrentLimit(ALGAE_WRIST_CURRENT_STALL_LIMIT)
            .voltageCompensation(ALGAE_WRIST_COMPENSATED_VOLTAGE)
            .idleMode(IdleMode.kBrake)
            .limitSwitch
            .apply(forwardLimitSwitchConfig)
            .apply(reverseLimitSwitchConfig);
            
            
        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        

        

        set(getRotation());

        pid.setTolerance(ALGAE_WRIST_TOLERENCE);
       
    
        commands = new AlgaeWristCommands(this);
    }

    @Override
    public void periodic(){
        double output = pid.calculate(getRotation()); 
        motor.set(output);

        SmartDashboard.putNumber(DASHBOARD_PREFIX + "rotation", getRotation());
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "output", output);
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "setPoint", pid.getGoal().position);
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "fowardSwitch", forwardLimitSwitch.isPressed());
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "reverseSwitch", reverseLimitSwitch.isPressed());
    }

    public void set(double position){
        pid.setGoal(position);
    }

    public void reset(){
        pid.setGoal(getRotation()); // Stops the wrist from moving
        pid.reset(getRotation());   //sets the error to zero but asssums it has no velocity
    }

    private double getRotation(){
        return Utils.absoluteRotation(absoluteEncoder.get() * 360 -ALGAE_WRIST_ENCODER_OFFSET);
    }

    public boolean atPosition(){
        return pid.atGoal();
    }

    public AlgaeWristCommands getCommands(){
        return commands;
    }

}

