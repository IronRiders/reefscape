package org.ironriders.climb;


import org.ironriders.lib.IronSubsystem;

import static org.ironriders.climb.ClimbConstants.ROTATION_MAXDOWN;
import static org.ironriders.climb.ClimbConstants.ROTATION_MAXUP;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;


import com.revrobotics.spark.config.SparkMaxConfig;

public class ClimbSubsystem extends IronSubsystem {

    private final SparkMax climbMotor = new SparkMax(ClimbConstants.CLIMBER_MOTOR_CAN_ID,
            SparkLowLevel.MotorType.kBrushless);
    private final SparkMaxConfig climbMotorConfig = new SparkMaxConfig();
    RelativeEncoder encoder = climbMotor.getEncoder();
    boolean reachedTopLimit = false;
    boolean reachedBottomLimit = false;
    

    private final ClimbCommands commands;

    public ClimbSubsystem() {
        climbMotorConfig.idleMode(IdleMode.kBrake); 
        climbMotorConfig.smartCurrentLimit(ClimbConstants.CURRENT_LIMIT);
        climbMotor.configure(climbMotorConfig, ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);
                
        commands = new ClimbCommands(this);


    }

    @Override
    public void periodic() {
        publish("Climber/encoder", encoder.getPosition());
  
            if(encoder.getPosition() < ROTATION_MAXUP){ 
                reachedTopLimit = true;
            } else {
                reachedTopLimit = false;
            }

            if(encoder.getPosition() > ROTATION_MAXDOWN){
                reachedBottomLimit = true;
            } else {
                reachedBottomLimit = false;
            }
        }
    

    public void set(ClimbConstants.State state) {
        if(state.speed < 0){
            if(reachedTopLimit){ 
                climbMotor.set(0);
            } else {
                climbMotor.set(state.speed);


            }
        } else {
            if(reachedBottomLimit){
                climbMotor.set(0);
            } else {
                climbMotor.set(state.speed); 
            }
        }
    }

    public ClimbCommands getCommands() {
        return commands;
    }


    public void rezero(){
        encoder.setPosition(0);
    }
}
