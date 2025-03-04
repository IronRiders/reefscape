package org.ironriders.elevator;

import static org.ironriders.elevator.ElevatorConstants.BOTTOM_POS;
import static org.ironriders.elevator.ElevatorConstants.D;
import static org.ironriders.elevator.ElevatorConstants.ELEVATOR_MOTOR_STALL_LIMIT;
import static org.ironriders.elevator.ElevatorConstants.FOLLOW_MOTOR_ID;
import static org.ironriders.elevator.ElevatorConstants.I;
import static org.ironriders.elevator.ElevatorConstants.INCHES_PER_ROTATION;
import static org.ironriders.elevator.ElevatorConstants.MAX_POSITION;
import static org.ironriders.elevator.ElevatorConstants.MIN_POSITION;
import static org.ironriders.elevator.ElevatorConstants.P;
import static org.ironriders.elevator.ElevatorConstants.PRIMARY_MOTOR_ID;

import java.util.Optional;

import org.ironriders.algae.AlgaeIntakeConstants;
import org.ironriders.algae.AlgaeWristConstants;
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

    private final SparkMax primaryMotor; // lead motor
    private final SparkMax followerMotor;

    private final SparkLimitSwitch bottomLimitSwitch;

    private final RelativeEncoder encoder;
    private final TrapezoidProfile profile;
    private final ElevatorFeedforward feedforward;
    private final PIDController pidController;
    private TrapezoidProfile.State goalState = new TrapezoidProfile.State();
    private TrapezoidProfile.State setPointState = new TrapezoidProfile.State();

    private Level currentTarget = Level.Down;
    private boolean isHomed = false;

    public ElevatorSubsystem() {
        SmartDashboard.putNumber("Elevator P", ElevatorConstants.P);
        SmartDashboard.putNumber("Elevator I", ElevatorConstants.I);
        SmartDashboard.putNumber("Elevator D", ElevatorConstants.D);
        SmartDashboard.putNumber("Elevator Constant Voltage", 0);

        goalState.position = 0;
        primaryMotor = new SparkMax(PRIMARY_MOTOR_ID, MotorType.kBrushless); 
        followerMotor = new SparkMax(FOLLOW_MOTOR_ID, MotorType.kBrushless);

        bottomLimitSwitch = primaryMotor.getReverseLimitSwitch();

        SparkMaxConfig primaryConfig = new SparkMaxConfig();
        SparkMaxConfig followerConfig = new SparkMaxConfig();
        // LimitSwitchConfig limitSwitchConfig = new LimitSwitchConfig();
        LimitSwitchConfig forwardLimitSwitchConfig = new LimitSwitchConfig();
        LimitSwitchConfig reverseLimitSwitchConfig = new LimitSwitchConfig();

        encoder = primaryMotor.getEncoder();

        // limitSwitchConfig.forwardLimitSwitchEnabled(false)
        //         .forwardLimitSwitchType(Type.kNormallyClosed).reverseLimitSwitchEnabled(true).reverseLimitSwitchType(Type.kNormallyClosed);
        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true).forwardLimitSwitchType(Type.kNormallyClosed);
        reverseLimitSwitchConfig.reverseLimitSwitchEnabled(true).reverseLimitSwitchType(Type.kNormallyClosed);

        // disabledLimitSwitchConfig.forwardLimitSwitchEnabled(false).forwardLimitSwitchType(Type.kNormallyClosed);
        primaryConfig.idleMode(IdleMode.kBrake).smartCurrentLimit(ELEVATOR_MOTOR_STALL_LIMIT);
        primaryConfig.inverted(true); // probably make a constant out of this
        primaryConfig.apply(forwardLimitSwitchConfig);
        primaryConfig.apply(reverseLimitSwitchConfig);

        followerConfig.follow(ElevatorConstants.PRIMARY_MOTOR_ID, true);
        followerConfig.idleMode(IdleMode.kBrake).smartCurrentLimit(ELEVATOR_MOTOR_STALL_LIMIT);
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
        pidController.setTolerance(.01);
        commands = new ElevatorCommands(this);
    }

    public void setGoal(Level goal) {
        this.goalState = new TrapezoidProfile.State(goal.positionInches, 0d);
    }

    @Override
    public void periodic() {
        var constantVoltage = SmartDashboard.getNumber("Elevator Constant Voltage", 0);
        if (constantVoltage > 0 && constantVoltage < .5) {
            // This is a rather dangerous value for tuning purposes.  Try to
            // prevent damage by limiting to something that won't shoot top
            // stage through roof, and ignore downward values entirely
            primaryMotor.set(constantVoltage);

            this.reportWarning("Applied const elevator voltage" + constantVoltage);
        } else {
            move();
        }

        // update SmartDashboard
        updateTelemetry();
    }

    public void move() {
        // Calculate the next state and update the current state
        setPointState = profile.calculate(ElevatorConstants.T, setPointState, goalState);
        SmartDashboard.putNumber("Elevator set postion", setPointState.position);

        pidController.setP(SmartDashboard.getNumber("Elevator P", ElevatorConstants.P));
        pidController.setI(SmartDashboard.getNumber("Elevator I", ElevatorConstants.I));
        pidController.setD(SmartDashboard.getNumber("Elevator D", ElevatorConstants.D));

        // Only run if homed
        if (isHomed) {
            double pidOutput = pidController.calculate(getHeightInches(), setPointState.position);
            if (pidOutput == 0) {
                primaryMotor.stopMotor();
                return;
            }

            double ff = feedforward.calculate(setPointState.position, setPointState.velocity);

            double outputPower = MathUtil.clamp(pidOutput + ff, -ElevatorConstants.MAX_OUTPUT,
                    ElevatorConstants.MAX_OUTPUT);

            primaryMotor.set(outputPower);
            SmartDashboard.putNumber("Elevator PID output", pidOutput);
        }
    }

    public void setPositionInches(double inches) {
        if (!isHomed && inches > 0) {
            System.out.println("Warning: Elevator not homed! Home first before moving to positions.");
            return;
        }

        // Update goal state for motion profile
        goalState = new TrapezoidProfile.State(MathUtil.clamp(inches, MIN_POSITION, MAX_POSITION), 0);
    }

    public void stopMotor() {
        primaryMotor.set(0);
        pidController.reset();
    }

    public void reset(){
        setPositionInches(getHeightInches());
        stopMotor();
    }

    private void updateTelemetry() {
        SmartDashboard.putNumber("Elevator Height", getHeightInches());
        
        getDiagnostic("P", P);
        getDiagnostic("I", I);
        getDiagnostic("D", D);
        publish("Homed", isHomed);
        publish("State", currentTarget.toString());
        publish("Primary Motor Current", primaryMotor.getOutputCurrent());
        publish("Velocity", setPointState.velocity);
        publish("Forward Limit Switch", primaryMotor.getForwardLimitSwitch().isPressed());
        publish("Reverse Limit Switch", primaryMotor.getReverseLimitSwitch().isPressed());
        publish("Follower Motor Current", followerMotor.getOutputCurrent());
        publish("Current Position", setPointState.position);
        publish("Goal Position", goalState.position);
        publish("Primary Encoder", primaryMotor.getEncoder().getPosition());
        publish("Follower Encoder", followerMotor.getEncoder().getPosition());
    }

    public double getHeightInches() {
        return encoder.getPosition() * INCHES_PER_ROTATION;
    }

    /**
     * Record current position as home.  A little goofy to have this driven outside the subsystem but homing needs
     * commands and our current command<->subsystem separation makes this necessary.
     */
    public void findHome() {
        if (bottomLimitSwitch.isPressed()) {
            primaryMotor.set(0);
            encoder.setPosition(0);
            isHomed = true;
            System.out.println("ELEVATOR HOMED");
            return;
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
