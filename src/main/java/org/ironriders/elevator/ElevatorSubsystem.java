package org.ironriders.elevator;

import static org.ironriders.elevator.ElevatorConstants.D;
import static org.ironriders.elevator.ElevatorConstants.ELEVATOR_MOTOR_STALL_LIMIT;
import static org.ironriders.elevator.ElevatorConstants.ELEVATOR_POSITION_TOLERANCE;
import static org.ironriders.elevator.ElevatorConstants.FOLLOW_MOTOR_ID;
import static org.ironriders.elevator.ElevatorConstants.I;
import static org.ironriders.elevator.ElevatorConstants.INCHES_PER_ROTATION;
import static org.ironriders.elevator.ElevatorConstants.MAX_POSITION;
import static org.ironriders.elevator.ElevatorConstants.MIN_POSITION;
import static org.ironriders.elevator.ElevatorConstants.P;
import static org.ironriders.elevator.ElevatorConstants.PRIMARY_MOTOR_ID;

import org.ironriders.elevator.ElevatorConstants.Level;
import org.ironriders.lib.IronSubsystem;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.LimitSwitchConfig.Type;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This subsystem controls the big ol' elevator that moves the algae and coral
 * manipulators vertically.
 */
public class ElevatorSubsystem extends IronSubsystem {

    private final ElevatorCommands commands;

    private final SparkMax primaryMotor = new SparkMax(PRIMARY_MOTOR_ID, MotorType.kBrushless); // lead motor
    private final SparkMax followerMotor = new SparkMax(FOLLOW_MOTOR_ID, MotorType.kBrushless);

    private final SparkLimitSwitch bottomLimitSwitch = primaryMotor.getReverseLimitSwitch();

    private final RelativeEncoder encoder = primaryMotor.getEncoder();

    private final TrapezoidProfile profile;
    private final ElevatorFeedforward feedforward;
    private final PIDController pidController;

    // goalSetpoint is the final goal. periodicSetpoint is a sort-of inbetween setpoint generated every periodic.
    private TrapezoidProfile.State goalSetpoint = new TrapezoidProfile.State();
    private TrapezoidProfile.State periodicSetpoint = new TrapezoidProfile.State();

    private Level currentTarget = Level.Down;
    private boolean isHomed = false;

    public ElevatorSubsystem() {

        SparkMaxConfig primaryConfig = new SparkMaxConfig();
        SparkMaxConfig followerConfig = new SparkMaxConfig();

        LimitSwitchConfig forwardLimitSwitchConfig = new LimitSwitchConfig()
            .forwardLimitSwitchEnabled(true)
            .forwardLimitSwitchType(Type.kNormallyClosed);
        LimitSwitchConfig reverseLimitSwitchConfig = new LimitSwitchConfig()
            .reverseLimitSwitchEnabled(true)
            .reverseLimitSwitchType(Type.kNormallyClosed);

        primaryConfig
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(ELEVATOR_MOTOR_STALL_LIMIT)
            .inverted(true)
            .apply(forwardLimitSwitchConfig)
            .apply(reverseLimitSwitchConfig);

        followerConfig
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(ELEVATOR_MOTOR_STALL_LIMIT)
            .follow(ElevatorConstants.PRIMARY_MOTOR_ID, true);

        primaryMotor.configure(primaryConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        followerMotor.configure(primaryConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        profile = new TrapezoidProfile(
            new TrapezoidProfile.Constraints(ElevatorConstants.MAX_VEL, ElevatorConstants.MAX_ACC));
        
        pidController = new PIDController(
                ElevatorConstants.P,
                ElevatorConstants.I,
                ElevatorConstants.D);

        feedforward = new ElevatorFeedforward(ElevatorConstants.K_S, ElevatorConstants.K_G, ElevatorConstants.K_V);
        pidController.setTolerance(ELEVATOR_POSITION_TOLERANCE);
        commands = new ElevatorCommands(this);
    }

    @Override
    public void periodic() {
        // Calculate the next state and update the current state
        periodicSetpoint = profile.calculate(ElevatorConstants.T, periodicSetpoint, goalSetpoint);

        // Only run if homed
        if (isHomed) {
            double pidOutput = pidController.calculate(getHeightInches(), periodicSetpoint.position);
            double ff = feedforward.calculate(periodicSetpoint.position, periodicSetpoint.velocity);

            primaryMotor.set(pidOutput + ff);
        }

        // update SmartDashboard
        updateTelemetry();
    }

    private void updateTelemetry() {

        publish("Homed", isHomed);
        publish("Goal State", currentTarget.toString());
        publish("Goal Position", goalSetpoint.position);
        
        publish("Forward Limit Switch", primaryMotor.getForwardLimitSwitch().isPressed());
        publish("Reverse Limit Switch", primaryMotor.getReverseLimitSwitch().isPressed());
        
        publish("Primary Encoder", primaryMotor.getEncoder().getPosition());
        publish("Follower Encoder", followerMotor.getEncoder().getPosition());
    }

    public void setGoal(Level goal) {
        this.goalSetpoint = new TrapezoidProfile.State(goal.positionInches, 0d);
    }

    public void setMotor(double set) {
        primaryMotor.set(set);
        pidController.setSetpoint(set);
    }

    public void stopMotor() {
        primaryMotor.set(0);
        pidController.reset();
    }

    public SparkLimitSwitch getBottomLimitSwitch() {
        return bottomLimitSwitch;
    }

    public void reset() {
        stopMotor();
    }

    public double getHeightInches() {
        return encoder.getPosition() * INCHES_PER_ROTATION;
    }

    public boolean isAtPosition(ElevatorConstants.Level level) {
        return Math.abs(getHeightInches() - level.positionInches) < 0.15;
    }

    public void reportHomed() {
        isHomed = true;
        encoder.setPosition(0); // reset
    }

    public boolean isHomed() {
        return isHomed;
    }

    public ElevatorCommands getCommands() {
        return commands;
    }
}
