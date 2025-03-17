package org.ironriders.climb;

import org.ironriders.lib.IronSubsystem;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SoftLimitConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ClimbSubsystem extends IronSubsystem {

    private final SparkMax climbMotor = new SparkMax(ClimbConstants.CLIMBER_MOTOR_CAN_ID,
            SparkLowLevel.MotorType.kBrushless);
    private final SparkMaxConfig climbMotorConfig = new SparkMaxConfig();

    private final ClimbCommands commands;


    private SoftLimitConfig softLimitConfig = new SoftLimitConfig(); // should force stop motor if it gets out of bounds

    public ClimbSubsystem() {
        climbMotorConfig.idleMode(IdleMode.kBrake); 
        climbMotorConfig.smartCurrentLimit(ClimbConstants.CURRENT_LIMIT);
        climbMotor.configure(climbMotorConfig.apply(softLimitConfig), ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        commands = new ClimbCommands(this);
    }

    @Override
    public void periodic() {

    }

    public void set(ClimbConstants.State state) {
        climbMotor.set(state.speed);
    }

    public ClimbCommands getCommands() {
        return commands;
    }
}
