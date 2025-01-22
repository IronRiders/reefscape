package org.ironriders.elevator;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfigAccessor;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.cscore.CameraServerJNI.LoggerFunction;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static org.ironriders.core.Constants.leftElevatorMotorDeviceID;
import static org.ironriders.core.Constants.rightElevatorMotorDeviceID;

public class ElevatorSubsystem extends SubsystemBase{
    private final double p = ;
    private final double i = ;
    private final double d = ;
    private SparkMax leftMotor; // lead motor
    private SparkMax rightMotor;

    private RelativeEncoder encoder;

    public ElevatorSubsystem(){
        leftMotor =  new SparkMax(leftElevatorMotorDeviceID, MotorType.kBrushless);
        rightMotor  =  new SparkMax(rightElevatorMotorDeviceID, MotorType.kBrushless)

        SparkMaxConfig leftConfig = new SparkMaxConfig();
        SparkMaxConfig rightConfig = new SparkMaxConfig();

        leftMotor.getClosedLoopController() ;
        encoder = leftMotor.getEncoder();

        leftConfig.idleMode(IdleMode.kBrake);
        leftConfig.closedLoop.p(p);
        leftConfig.closedLoop.i(i);
        leftConfig.closedLoop.d(d);

        rightConfig.follow(leftElevatorMotorDeviceID);
        rightConfig.idleMode(IdleMode.kBrake);
        rightConfig.inverted(true);

        leftMotor.configure(leftConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
        rightMotor.configure(rightConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters); 
    }

    public void setGoal(Level goal){
        leftMotor.getClosedLoopController().setReference(rightElevatorMotorDeviceID, null)
    }

    //https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/trapezoidal-profiles.html#trapezoidal-motion-profiles-in-wpilib

    //https://docs.revrobotics.com/brushless/revlib/revlib-overview/migrating-to-revlib-2025

    //https://docs.revrobotics.com/brushless/revlib/closed-loop-control-overview/getting-started-with-pid-tuning
}
