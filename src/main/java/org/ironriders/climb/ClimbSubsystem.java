package org.ironriders.climb;


import static org.ironriders.elevator.ElevatorConstants.GEAR_RATIO;
import static org.ironriders.elevator.ElevatorConstants.MAX_POSITION;

import java.util.DuplicateFormatFlagsException;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkRelativeEncoder;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

public class ClimbSubsystem extends SubsystemBase {

    private final SparkMax climbMotor = new SparkMax(ClimbConstants.CLIMBER_MOTOR_CAN_ID, SparkLowLevel.MotorType.kBrushless);
    private final SparkMaxConfig climbMotorConfigBrake = new SparkMaxConfig();
    private final SparkMaxConfig climbMotorConfigCoast = new SparkMaxConfig();
    private final ClimbCommands commands;
    private final RelativeEncoder encoder;

    double currentPostion;
    double oldValue;
    double newValue;
    boolean staysUp;

    public ClimbSubsystem() {
        encoder = climbMotor.getEncoder();
        climbMotorConfigCoast // if there's an issue with breaks and stuff, the docs show that limit switch is enabled by default
                .smartCurrentLimit(ClimbConstants.CURRENT_LIMIT)
                //.voltageCompensation(ClimbContstants.COMPENSATION)
                .idleMode(IdleMode.kCoast);
        climbMotor.configure(climbMotorConfigCoast,ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);
            
        climbMotorConfigBrake // if there's an issue with breaks and stuff, the docs show that limit switch is enabled by default
                .smartCurrentLimit(ClimbConstants.CURRENT_LIMIT)
                //.voltageCompensation(ClimbContstants.COMPENSATION)
                .idleMode(IdleMode.kBrake)
                .softLimit.forwardSoftLimit(MAX_POSITION);
        // climbMotor.configure(climbMotorConfigBrake,ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);

        commands = new ClimbCommands(this);
    }

    @Override
    public void periodic() {
        newValue = encoder.getPosition();
        updatePostion();
        if((oldValue > newValue) && staysUp) {
            double compensate = (oldValue-newValue) * ClimbConstants.FAKE_PID_COMPENSATION;
            
            climbMotor.set(compensate);

            System.out.println("Compensating for climber falling by seting motor to " + compensate);
            SmartDashboard.putNumber("Climber Compensation", compensate);
        }
        oldValue = newValue;
        System.out.println("(Climber): Not using auto up");
    }

    public void brakeClimber(boolean breakOn){
        if(breakOn){
            climbMotor.configure(climbMotorConfigBrake,ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);
        }
        else{
            climbMotor.configure(climbMotorConfigCoast,ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);
        }
    }

    public void updatePostion(){
        currentPostion = encoder.getPosition() * GEAR_RATIO;
    }

    public void set(ClimbConstants.State state) {
        climbMotor.set(state.speed);

        if(state == ClimbConstants.State.UP || state == ClimbConstants.State.STOP) {
            staysUp = true;
            SmartDashboard.putBoolean("Climber Compensation Mode", staysUp);
        }
        else {
            staysUp = false;
            SmartDashboard.putBoolean("Climber Compensation Mode", staysUp);
        }
    }

    public ClimbCommands getCommands() {
        return commands;
    }
}
