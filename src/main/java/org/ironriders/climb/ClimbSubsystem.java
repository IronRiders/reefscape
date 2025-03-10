package org.ironriders.climb;
import java.util.DuplicateFormatFlagsException;
import org.ironriders.lib.IronSubsystem;
import org.ironriders.lib.data.PID;
import org.ironriders.wrist.algae.AlgaeWristConstants;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkRelativeEncoder;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SoftLimitConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

public class ClimbSubsystem extends IronSubsystem {

    private final TrapezoidProfile profile;
    private final PIDController pidController;

    private final SparkMax climbMotor = new SparkMax(ClimbConstants.CLIMBER_MOTOR_CAN_ID,
            SparkLowLevel.MotorType.kBrushless);
    private final SparkMaxConfig climbMotorConfig = new SparkMaxConfig();
    
    private final ClimbCommands commands;
    private final RelativeEncoder encoder;

    private TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State();
    private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State();

    
    public ClimbSubsystem() {
        encoder = climbMotor.getEncoder();
        encoder.setPosition(0);

        var softLimitConfig = new SoftLimitConfig();
        softLimitConfig
            .reverseSoftLimitEnabled(true)
            .reverseSoftLimit(ClimbConstants.Targets.MAX.pos)
            .forwardSoftLimitEnabled(true)
            .forwardSoftLimit(ClimbConstants.Targets.HOME.pos);


        climbMotor.configure(climbMotorConfig.apply(softLimitConfig),ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);
        
        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(ClimbConstants.MAX_VEL, ClimbConstants.MAX_ACC);
        profile = new TrapezoidProfile(constraints);
        goalSetpoint = new TrapezoidProfile.State();
        pidController = new PIDController(
                ClimbConstants.P,
                ClimbConstants.I,
                ClimbConstants.D);

        pidController.setTolerance(.01);

        commands = new ClimbCommands(this);
    }

    @Override
    public void periodic() {
        publish("Climb Motor Val", getPostion());
    
        periodicSetpoint = profile.calculate(ClimbConstants.T, periodicSetpoint, goalSetpoint);
        publish("Climber set postion", periodicSetpoint.position);

        pidController.setP(SmartDashboard.getNumber("Climber P", ClimbConstants.P));
        pidController.setI(SmartDashboard.getNumber("Climber I", ClimbConstants.I));
        pidController.setD(SmartDashboard.getNumber("Climber D", ClimbConstants.D));
    }

    public double getPostion() {
        return encoder.getPosition() * ClimbConstants.GEAR_RATIO;
    }

    public void set(ClimbConstants.State state) {
        climbMotor.set(state.speed);
    }

    public void goTo(ClimbConstants.Targets limit) {
        setGoal(limit);

        double pidOutput = pidController.calculate(getPostion(), periodicSetpoint.position);
        if (pidOutput == 0) {
            climbMotor.stopMotor();
            return;
        }

        climbMotor.set(pidOutput);
        publish("Climber PID output", pidOutput);
    }

    public void setGoal(ClimbConstants.Targets limit) {
        this.goalSetpoint = new TrapezoidProfile.State(limit.pos, 0d);
    }

    public void reZero() {
        encoder.setPosition(0);
    }

    public ClimbCommands getCommands() {
        return commands;
    }
}
