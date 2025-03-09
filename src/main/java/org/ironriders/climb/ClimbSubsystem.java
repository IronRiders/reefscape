package org.ironriders.climb;

import static org.ironriders.elevator.ElevatorConstants.GEAR_RATIO;
import static org.ironriders.elevator.ElevatorConstants.MAX_POSITION;

import java.util.DuplicateFormatFlagsException;

import org.ironriders.lib.IronSubsystem;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkRelativeEncoder;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SoftLimitConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

public class ClimbSubsystem extends IronSubsystem {

    private final SparkMax climbMotor = new SparkMax(ClimbConstants.CLIMBER_MOTOR_CAN_ID,
            SparkLowLevel.MotorType.kBrushless);
    private final SparkMaxConfig climbMotorConfigBrake = new SparkMaxConfig();
    
    private final ClimbCommands commands;
    private final RelativeEncoder encoder;

    double currentPostion;
    double oldValue;
    double newValue;
    boolean staysUp;

    public ClimbSubsystem() {
        encoder = climbMotor.getEncoder();
        encoder.setPosition(0);

        var softLimitConfig = new SoftLimitConfig();

        // I *think* soft limits are set in rotations of the internal encoder,
        // not the absolute encoder.  This isn't documented.  However, on current
        // bot, disabling limit is worse than crashing because current is low and
        // if the motor disables the arm will crash into bearing brackets on
        // elevator.  So leaving disabled for now
        softLimitConfig
            .reverseSoftLimitEnabled(true)
            .reverseSoftLimit(ClimbConstants.Limits.MAX.pos)
            .forwardSoftLimitEnabled(true)
            .forwardSoftLimit(ClimbConstants.Limits.HOME.pos);


        climbMotor.configure(climbMotorConfigBrake.apply(softLimitConfig),ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);

        SmartDashboard.putNumber("Climber Compensation", 0);
        

        commands = new ClimbCommands(this);
    }

    @Override
    public void periodic() {
        publish("Climb Motor Val", climbMotor.getEncoder().getPosition());

        newValue = encoder.getPosition();
        updatePostion();
        if ((oldValue > newValue) && staysUp) {
            double compensate = (oldValue - newValue) * ClimbConstants.GEAR_RATIO;

            climbMotor.set(compensate);

            System.out.println("Compensating for climber falling by seting motor to " + compensate);
            SmartDashboard.putNumber("Climber Compensation", compensate);

        }

        oldValue = newValue;
        // System.out.println("(Climber): Not using auto up");

    }

    public void updatePostion() {
        currentPostion = encoder.getPosition() * GEAR_RATIO;
    }

    public void set(ClimbConstants.State state) {
        climbMotor.set(state.speed);

        if (state == ClimbConstants.State.UP || state == ClimbConstants.State.STOP) {
            staysUp = false;
            SmartDashboard.putBoolean("Climber Compensation Mode", staysUp);
        } else {
            staysUp = false;
            SmartDashboard.putBoolean("Climber Compensation Mode", staysUp);
        }
    }

    public ClimbCommands getCommands() {
        return commands;
    }
}
