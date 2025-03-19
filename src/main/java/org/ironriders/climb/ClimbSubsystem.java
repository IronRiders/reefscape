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

    private final TrapezoidProfile profile;
    private final PIDController pidController;

    private final SparkMax climbMotor = new SparkMax(ClimbConstants.CLIMBER_MOTOR_CAN_ID,
            SparkLowLevel.MotorType.kBrushless);
    private final SparkMaxConfig climbMotorConfig = new SparkMaxConfig();

    private final ClimbCommands commands;
    private final RelativeEncoder encoder;

    private double pidOutput;

    private TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State();
    private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State();

    private SoftLimitConfig softLimitConfig = new SoftLimitConfig(); // should force stop motor if it gets out of bounds

    public ClimbSubsystem() {
        publish("Climber P", ClimbConstants.P);
        publish("Climber I", ClimbConstants.I);
        publish("Climber D", ClimbConstants.D);

        encoder = climbMotor.getEncoder();
        encoder.setPosition(0); // Set pos to zero on deploy

        softLimitConfig
                .reverseSoftLimitEnabled(true)
                .reverseSoftLimit(ClimbConstants.Targets.MAX.pos)
                .forwardSoftLimitEnabled(true)
                .forwardSoftLimit(ClimbConstants.Targets.HOME.pos); // Home is also the minimum position

        climbMotorConfig.idleMode(IdleMode.kCoast); // for testing set to coast 
        climbMotorConfig.smartCurrentLimit(ClimbConstants.CURRENT_LIMIT);
        climbMotor.configure(climbMotorConfig.apply(softLimitConfig), ResetMode.kResetSafeParameters,
                PersistMode.kPersistParameters);

        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(ClimbConstants.MAX_VEL,
                ClimbConstants.MAX_ACC);
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
        periodicSetpoint = profile.calculate(ClimbConstants.T, periodicSetpoint, goalSetpoint);

        publish("Climber set postion", periodicSetpoint.position);
        //publish("Climb Motor Val", getPostion());
        publish("Climb Motor Val", encoder.getPosition()); // for testing

        publish("Climber target pos", goalSetpoint.position);
        publish("Climber target velo", goalSetpoint.velocity);
        publish("Climber PID output", pidOutput);

        pidController.setP(SmartDashboard.getNumber("Climber P", ClimbConstants.P));
        pidController.setI(SmartDashboard.getNumber("Climber I", ClimbConstants.I));
        pidController.setD(SmartDashboard.getNumber("Climber D", ClimbConstants.D));
    }

    public double getPostion() {
        return encoder.getPosition() * ClimbConstants.GEAR_RATIO;
    }

    public void set(ClimbConstants.State state) {
        System.out.println(
                "(Climber) Warning! Someone directly set the climber speed. This can (and has) broken the climber! Use goTo() if possible!");
        climbMotor.set(state.speed);
    }

    public void goTo(ClimbConstants.Targets limit) {
        setGoal(limit);

        pidOutput = pidController.calculate(getPostion() /* Encoder pos times motor gearing */,
                periodicSetpoint.position);

        if (pidOutput == 0) {
            climbMotor.stopMotor();
                return;
        }
        
        climbMotor.set(pidOutput);
    }

    /*
    public void setGoal(double pos){
        this.goalSetpoint = new TrapezoidProfile.State(pos, 0d);
    }
    */

    public double getGoal() {
        return goalSetpoint.position;
    }

    public void setGoal(ClimbConstants.Targets limit) {
        this.goalSetpoint = new TrapezoidProfile.State(limit.pos, 0d);
    }

    public void reZero() {
        encoder.setPosition(0);
        periodicSetpoint = new TrapezoidProfile.State(0, 0d);
        goalSetpoint = new TrapezoidProfile.State(0, 0d);
    }

    public ClimbCommands getCommands() {
        return commands;
    }
}
