package org.ironriders.elevator;

import static org.ironriders.elevator.ElevatorConstants.leftElevatorMotorDeviceID;
import static org.ironriders.elevator.ElevatorConstants.rightElevatorMotorDeviceID;

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

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static org.ironriders.elevator.ElevatorConstants.*;
public class ElevatorSubsystem extends SubsystemBase{
    //private final double ff = 0;
    private final SparkMax leftMotor; // lead motor
    private final SparkMax rightMotor;
    private final SparkClosedLoopController PIDController;
    private final SparkLimitSwitch topLimitSwitch;
    private final SparkLimitSwitch bottomLimitSwitch;
    private final RelativeEncoder encoder;
    private final TrapezoidProfile profile;
    private final ElevatorFeedforward feedforward;
    private TrapezoidProfile.State goal;
    private TrapezoidProfile.State setPoint;

    public ElevatorSubsystem(){
        leftMotor =  new SparkMax(leftElevatorMotorDeviceID, MotorType.kBrushless);
        rightMotor  =  new SparkMax(rightElevatorMotorDeviceID, MotorType.kBrushless);

        topLimitSwitch = leftMotor.getForwardLimitSwitch();
        bottomLimitSwitch = leftMotor.getReverseLimitSwitch();

        PIDController = leftMotor.getClosedLoopController();

        SparkMaxConfig leftConfig = new SparkMaxConfig();
        SparkMaxConfig rightConfig = new SparkMaxConfig();

        LimitSwitchConfig topLimitSwitchConfig = new LimitSwitchConfig();
        LimitSwitchConfig bottomLimitSwitchConfig = new LimitSwitchConfig();

        encoder = leftMotor.getEncoder();

        leftConfig.idleMode(IdleMode.kBrake);
        leftConfig.closedLoop.pid(p, i, d);
        leftConfig.closedLoop.iZone(iZone);

        rightConfig.follow(leftElevatorMotorDeviceID);
        rightConfig.idleMode(IdleMode.kBrake);
        rightConfig.inverted(true);

        leftMotor.configure(leftConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
        rightMotor.configure(rightConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);

        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(maxVel, maxAcc);
        profile = new TrapezoidProfile(constraints);
        goal = new TrapezoidProfile.State();
        setPoint = new TrapezoidProfile.State();

        feedforward = new ElevatorFeedforward(kS, kG, kV);
    }

    public void setGoal(Level goal){
        //PIDController.setReference(goal.height, ControlType.kPosition, ClosedLoopSlot.kSlot0, 0); // arb feedforward should be calculated 

        this.goal = new TrapezoidProfile.State(goal.height, 0d);
    }

    @Override
    public void periodic(){
        if(topLimitSwitch.isPressed()){
            leftMotor.stopMotor();
        }
        else if(bottomLimitSwitch.isPressed()){
            leftMotor.stopMotor();
        }

        setPoint = profile.calculate(t, setPoint, goal);

        PIDController.setReference(goal.position, ControlType.kPosition, ClosedLoopSlot.kSlot0, feedforward.calculate(ffVel, ffAcc));
    }
    //https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/trapezoidal-profiles.html#trapezoidal-motion-profiles-in-wpilib

    //https://docs.revrobotics.com/brushless/revlib/revlib-overview/migrating-to-revlib-2025

    //https://docs.revrobotics.com/brushless/revlib/closed-loop-control-overview/getting-started-with-pid-tuning

    //https://docs.revrobotics.com/brushless/revlib/closed-loop-control-overview/closed-loop-control-getting-started#pid-constants-and-configuration
}
