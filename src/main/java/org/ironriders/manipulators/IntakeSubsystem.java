package org.ironriders.manipulators;

import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static org.ironriders.manipulators.IntakeConstants.*;

import org.ironriders.manipulators.IntakeConstants.State;;



public class IntakeSubsystem extends SubsystemBase{
    private final IntakeCommands commands;
    //find acutal motor IDs
    private final SparkMax coralMotor = new SparkMax(CORALINTAKEMOTOR, MotorType.kBrushless);
    private final SparkMax algaeLeftMotor = new SparkMax(ALGAELEFTINTAKEMOTOR, MotorType.kBrushless);
    private final SparkMax algaeRightMotor = new SparkMax(ALGAERIGHTINTAKEMOTOR, MotorType.kBrushless);
    private final SparkMaxConfig coralMotorConfig = new SparkMaxConfig();
    private final SparkMaxConfig algaeMotorConfig = new SparkMaxConfig();
    public IntakeSubsystem(){
        coralMotorConfig
            .smartCurrentLimit(CORAL_INTAKE_CURRENT_STALL_LIMIT)
            .voltageCompensation(CORAL_INTAKE_COMPENSATED_VOLTAGE)
            .idleMode(IdleMode.kBrake);
        coralMotor.configure(coralMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        algaeMotorConfig
            .smartCurrentLimit(ALGAE_INTAKE_CURRENT_STALL_LIMIT)
            .voltageCompensation(ALGAE_INTAKE_COMPENSATED_VOLTAGE)
            .idleMode(IdleMode.kBrake);
        algaeLeftMotor.configure(algaeMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        algaeRightMotor.configure(algaeMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        commands = new IntakeCommands(this);
    
    }

    @Override
    public void periodic(){

        SmartDashboard.putNumber(DASHBOARD_PREFIX + "velocity", getSpeed());       
        
    }


    public void set(State state){
        algaeLeftMotor.set(state.getSpeed());
        algaeRightMotor.set(-state.getSpeed());

        SmartDashboard.putString(DASHBOARD_PREFIX + "state", state.name());
    }

    private double getSpeed(){
        return algaeLeftMotor.getEncoder().getVelocity();
    }

    public void reset(){
       set(State.STOP);
    }

}
