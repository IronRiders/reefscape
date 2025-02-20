package org.ironriders.elevator;

import com.ctre.phoenix6.signals.ForwardLimitSourceValue;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.LimitSwitchConfig.Type;

import org.ironriders.elevator.ElevatorConstants.*;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static org.ironriders.elevator.ElevatorConstants.*;

public class ElevatorSubsystem extends SubsystemBase {

    private final ElevatorCommands commands;

    // private final double ff = 0;
    private final SparkMax primaryMotor; // lead motor
    private final SparkMax followerMotor;

    private final SparkLimitSwitch topLimitSwitch;
    private final SparkLimitSwitch bottomLimitSwitch;

    private final RelativeEncoder encoder;
    private final TrapezoidProfile profile;
    private final ElevatorFeedforward feedforward;
    private final PIDController pidController;
    private TrapezoidProfile.State goalState = new TrapezoidProfile.State();
    private TrapezoidProfile.State currentState = new TrapezoidProfile.State();
    

    private Level currentTarget = Level.Down;
    private boolean isHomed = false;
    private double setpoint = 0.0;

    public ElevatorSubsystem() {
        goalState.position =0;
        primaryMotor = new SparkMax(PRIMARY_MOTOR_ID, MotorType.kBrushless); 
        followerMotor = new SparkMax(FOLLOW_MOTOR_ID, MotorType.kBrushless);

        topLimitSwitch = primaryMotor.getReverseLimitSwitch();
        bottomLimitSwitch = primaryMotor.getForwardLimitSwitch();

        SparkMaxConfig primaryConfig = new SparkMaxConfig();
        SparkMaxConfig followerConfig = new SparkMaxConfig();
        LimitSwitchConfig limitSwitchConfig = new LimitSwitchConfig();
        LimitSwitchConfig disabledLimitSwitchConfig = new LimitSwitchConfig();

        encoder = primaryMotor.getEncoder();

        limitSwitchConfig.forwardLimitSwitchEnabled(false)
                .forwardLimitSwitchType(Type.kNormallyClosed).reverseLimitSwitchEnabled(true).reverseLimitSwitchType(Type.kNormallyClosed);

        // disabledLimitSwitchConfig.forwardLimitSwitchEnabled(false).forwardLimitSwitchType(Type.kNormallyClosed);
        primaryConfig.idleMode(IdleMode.kCoast).smartCurrentLimit(ELEVATOR_MOTOR_STALL_LIMIT);
        primaryConfig.inverted(true); // probably make a constant out of this
        primaryConfig.apply(limitSwitchConfig);

        followerConfig.follow(ElevatorConstants.PRIMARY_MOTOR_ID, true);
        followerConfig.idleMode(IdleMode.kCoast).smartCurrentLimit(ELEVATOR_MOTOR_STALL_LIMIT);
        // followerConfig.inverted(true); // probably make a constant out of this
        

        primaryMotor.configure(primaryConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        followerMotor.configure(followerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(ElevatorConstants.MAX_VEL, ElevatorConstants.MAX_ACC);
        profile = new TrapezoidProfile(constraints);
        goalState = new TrapezoidProfile.State();
        pidController = new PIDController(
                ElevatorConstants.P,
                ElevatorConstants.I,
                ElevatorConstants.D);

        feedforward = new ElevatorFeedforward(ElevatorConstants.K_S, ElevatorConstants.K_G, ElevatorConstants.K_V);
        commands = new ElevatorCommands(this);
    }

    public void setGoal(Level goal) {
        this.goalState = new TrapezoidProfile.State(goal.positionInches, 0d);
    }

    boolean isHoming = false;
    @Override
    public void periodic() {
        currentState.position = encoder.getPosition() * ElevatorConstants.INCHES_PER_ROTATION;
        // Calculate the next state and update the current state
        currentState = profile.calculate(ElevatorConstants.T, currentState, goalState);

        if (bottomLimitSwitch.isPressed()) {
            handleBottomLimit();
        }
        // if (isHoming =true && !bottomLimitSwitch.isPressed()){
        //     primaryMotor.set(0);
        //     isHoming = false;
        // }
        // } else if (topLimitSwitch.isPressed()) {
        //     stopMotor();
        // }

        // Only run if homed
        if (isHomed) {
            double pidOutput = pidController.calculate(getHeightInches(), goalState.position);
            double ff = calculateFeedForward(currentState);

            double ouputPower = MathUtil.clamp(pidOutput + ff, -ElevatorConstants.MAX_OUTPUT,
                    ElevatorConstants.MAX_OUTPUT);

            primaryMotor.set(ouputPower);
        }

        // update SmartDashboard
        updateTelemetry();
    }

    public void setPositionInches(double inches) {
        if (!isHomed && inches > 0) {
            System.out.println("Warning: Elevator not homed! Home first before moving to positions.");
            return;
        }

        setpoint = MathUtil.clamp(inches, MIN_POSITION, MAX_POSITION);

        // Update goal state for motion profile
        goalState = new TrapezoidProfile.State(setpoint, 0);
    }

    public void stopMotor() {
        primaryMotor.set(0);
        pidController.reset();
    }

    private void handleBottomLimit() {
        // stopMotor();
        encoder.setPosition(BOTTOM_POS * INCHES_PER_ROTATION);
        isHomed = true;
        // isHoming = true;
        // setpoint = BOTTOM_POS;
        currentState = new TrapezoidProfile.State(BOTTOM_POS, 0);
        // goalState = new TrapezoidProfile.State(BOTTOM_POS, 0);
        // LimitSwitchConfig limitSwitchConfig = new LimitSwitchConfig();
        // limitSwitchConfig.forwardLimitSwitchEnabled(false);
        // primaryMotor.set(.1);
    }

    private void updateTelemetry() {
        SmartDashboard.putNumber("Elevator Height", getHeightInches());
        SmartDashboard.putNumber("Elevator Target", setpoint);
        SmartDashboard.putBoolean("Elevator Homed", isHomed);
        SmartDashboard.putString("Elevator State", currentTarget.toString());
        SmartDashboard.putNumber("Elevator Primary Motor Current", primaryMotor.getOutputCurrent());
        SmartDashboard.putNumber("Elevator Velocity", currentState.velocity);
        SmartDashboard.putBoolean("Elevator Forward Limit Switch", primaryMotor.getForwardLimitSwitch().isPressed());
        SmartDashboard.putBoolean("Elevator Reverse Limit Switch", primaryMotor.getReverseLimitSwitch().isPressed());
        SmartDashboard.putNumber("Elevator Follower Motor Current", followerMotor.getOutputCurrent());
        SmartDashboard.putNumber("Elevator Setpoint", setpoint);
        SmartDashboard.putNumber("Elevator Current Position", currentState.position);
        SmartDashboard.putNumber("Elevator goal Position", goalState.position);

    }

    private double calculateFeedForward(TrapezoidProfile.State state) {
        // kS (static friction), kG (gravity), kV (velocity)
        return ElevatorConstants.K_S * Math.signum(state.velocity) +
                ElevatorConstants.K_G +
                ElevatorConstants.K_V * state.velocity;
    }

    public double getHeightInches() {
        return encoder.getPosition() / INCHES_PER_ROTATION;
    }

    public void homeElevator() {
        primaryMotor.set(-0.1); // Slow downward movement until bottom limit is hit
        System.out.println("ELEVATOR HOMED");
        if (bottomLimitSwitch.isPressed()) {
            handleBottomLimit();
        }
    }

    public boolean isAtPosition(ElevatorConstants.Level level) {
        return pidController.atSetpoint() &&
                Math.abs(getHeightInches() - level.positionInches) < 0.5;
    }

    public boolean isHomed() {
        return isHomed;
    }

    public ElevatorCommands getCommands() {
        return commands;
    }
}
