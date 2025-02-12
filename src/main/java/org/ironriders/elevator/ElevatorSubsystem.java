package org.ironriders.elevator;

import static org.ironriders.core.Constants.leftElevatorMotorDeviceID;
import static org.ironriders.core.Constants.rightElevatorMotorDeviceID;

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

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static org.ironriders.elevator.ElevatorConstants.*;
public class ElevatorSubsystem extends SubsystemBase{
    //private final double ff = 0;
    private final SparkMax primaryMotor; // lead motor
    private final SparkMax followerMotor;
    private final SparkClosedLoopController PIDController;
    private final SparkLimitSwitch topLimitSwitch;
    private final SparkLimitSwitch bottomLimitSwitch;
    private final RelativeEncoder encoder;
    private final TrapezoidProfile profile;
    private final ElevatorFeedforward feedforward;
    private final PIDController pidController;
    private TrapezoidProfile.State goalState;
    private TrapezoidProfile.State currentState;
    
    private Level currentTarget = Level.Down;
    private boolean isHomed = false;
    private double setpoint = 0.0;
    double currentPos;

    public ElevatorSubsystem(){
        primaryMotor =  new SparkMax(leftElevatorMotorDeviceID, MotorType.kBrushless);
        followerMotor  =  new SparkMax(rightElevatorMotorDeviceID, MotorType.kBrushless);

        topLimitSwitch = primaryMotor.getForwardLimitSwitch();
        bottomLimitSwitch = primaryMotor.getReverseLimitSwitch();

        PIDController = primaryMotor.getClosedLoopController();

        SparkMaxConfig primaryConfig = new SparkMaxConfig();
        SparkMaxConfig followerConfig = new SparkMaxConfig();

        LimitSwitchConfig topLimitSwitchConfig = new LimitSwitchConfig();
        LimitSwitchConfig bottomLimitSwitchConfig = new LimitSwitchConfig();

        encoder = primaryMotor.getEncoder();

        primaryConfig.idleMode(IdleMode.kBrake);
        

        followerConfig.follow(leftElevatorMotorDeviceID);
        followerConfig.idleMode(IdleMode.kBrake);
        followerConfig.inverted(true);

        primaryMotor.configure(primaryConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
        followerMotor.configure(followerConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(maxVel, maxAcc);
        profile = new TrapezoidProfile(constraints);
        goalState = new TrapezoidProfile.State();

        pidController = new PIDController(
            ElevatorConstants.p,
            ElevatorConstants.i,
            ElevatorConstants.d
        );


        feedforward = new ElevatorFeedforward(kS, kG, kV);
    }

    public void setGoal(Level goal){
        //PIDController.setReference(goal.height, ControlType.kPosition, ClosedLoopSlot.kSlot0, 0); // arb feedforward should be calculated 

        this.goalState = new TrapezoidProfile.State(goal.positionInches, 0d);
    }

    @Override
    public void periodic(){
        currentPos = encoder.getPosition() / inchesPerRotation;
        //Calculate the next state and update the current state
        currentState = profile.calculate(t, currentState, goalState);
        

        if(bottomLimitSwitch.isPressed()){
            handleBottomLimit();
        } 
        else if(topLimitSwitch.isPressed()){
            stopMotor();
        }

        // Only run if homed
        if(isHomed) {
            double pidOutput = pidController.calculate(getHeightInches(), currentState.position);
            double ff = calculateFeedForward(currentState);

            double ouputPower = MathUtil.clamp(pidOutput + ff, -ElevatorConstants.max_output, ElevatorConstants.max_output);

            primaryMotor.set(ouputPower);
        }
    
        //update SmartDashboard
        updateTelemetry();
    }

    public void setPositionInches(double inches){
        if(!isHomed && inches>0){
            System.out.println("Waraning: Elevator not homed! Home first before moving to positions.");
            return;
        }

        setpoint = MathUtil.clamp(inches, minPostiton, maxPosition);

        // Update goal state for motion profile
        goalState = new TrapezoidProfile.State(setpoint, 0);
    }

    public void stopMotor(){
        primaryMotor.set(0);
        pidController.reset();
    }

    private void handleBottomLimit(){
        stopMotor();
        encoder.setPosition(bottomPos* inchesPerRotation);
        isHomed = true;
        setpoint = bottomPos;
        currentState = new TrapezoidProfile.State(bottomPos, 0);
        goalState = new TrapezoidProfile.State(bottomPos,0);
    }

    private void updateTelemetry(){
        SmartDashboard.putNumber("Elevator Height", getHeightInches());
        SmartDashboard.putNumber("Elevator Target", setpoint);
        SmartDashboard.putBoolean("Elevator Homed", isHomed);
        SmartDashboard.putString("Elevator State", currentTarget.toString());
        SmartDashboard.putNumber("Elevator Current", primaryMotor.getOutputCurrent());
        SmartDashboard.putNumber("Elevator Velocity", currentState.velocity);
    }

    private double calculateFeedForward(TrapezoidProfile.State state){
        // kS (static friction),  kG (gravity), kV (velocity)
        return ElevatorConstants.kS * Math.signum(state.velocity) +
        ElevatorConstants.kG +
        ElevatorConstants.kV * state.velocity;
    }

    public double getHeightInches(){
        return encoder.getPosition() / inchesPerRotation;
    }

    public void homeElevator(){
        primaryMotor.set(-0.1); //Slow downward movement until bottom limit is hit
    }
    
    public boolean isAtPosition(Level level){
        return pidController.atSetpoint() &&
            Math.abs(getHeightInches() - level.positionInches) <0.5;
    }

    public boolean isHomed(){
        return isHomed;
    }

    //https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/trapezoidal-profiles.html#trapezoidal-motion-profiles-in-wpilib

    //https://docs.revrobotics.com/brushless/revlib/revlib-overview/migrating-to-revlib-2025

    //https://docs.revrobotics.com/brushless/revlib/closed-loop-control-overview/getting-started-with-pid-tuning

    //https://docs.revrobotics.com/brushless/revlib/closed-loop-control-overview/closed-loop-control-getting-started#pid-constants-and-configuration
}
