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

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static org.ironriders.elevator.ElevatorConstants.*;

import org.ironriders.drive.DriveConstants;

public class ElevatorSubsystem extends SubsystemBase {

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

    public ElevatorSubsystem() {
        leftMotor = new SparkMax(LEFT_MOTOR_ID, MotorType.kBrushless);
        rightMotor = new SparkMax(RIGHT_MOTOR_ID, MotorType.kBrushless);

        topLimitSwitch = leftMotor.getForwardLimitSwitch();
        bottomLimitSwitch = leftMotor.getReverseLimitSwitch();

        PIDController = leftMotor.getClosedLoopController();

        SparkMaxConfig leftConfig = new SparkMaxConfig();
        SparkMaxConfig rightConfig = new SparkMaxConfig();

        LimitSwitchConfig topLimitSwitchConfig = new LimitSwitchConfig();
        LimitSwitchConfig bottomLimitSwitchConfig = new LimitSwitchConfig();

        encoder = leftMotor.getEncoder();

        leftConfig.idleMode(IdleMode.kBrake);
        leftConfig.closedLoop.pid(MOTOR_PID_P, MOTOR_PID_I, MOTOR_PID_D);
        leftConfig.closedLoop.iZone(MOTOR_PID_IZONE);

        rightConfig.follow(LEFT_MOTOR_ID);
        rightConfig.idleMode(IdleMode.kBrake);
        rightConfig.inverted(true);

        leftMotor.configure(leftConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
        rightMotor.configure(rightConfig, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);

        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(MAXIMUM_VELOCITY,
                MAXIMUM_ACCELERATION);
        profile = new TrapezoidProfile(constraints);
        goal = new TrapezoidProfile.State();
        setPoint = new TrapezoidProfile.State();

        feedforward = new ElevatorFeedforward(KS, KG, KV);
    }

    public void setGoal(Level goal) {
        // PIDController.setReference(goal.height, ControlType.kPosition,
        // ClosedLoopSlot.kSlot0, 0); // arb feedforward should be calculated

        this.goal = new TrapezoidProfile.State(goal.height, 0d);
    }

    public boolean atGoal() {
        return Math.abs(encoder.getPosition() - goal.position) < GOAL_TOLERANCE;
    }

    @Override
    public void periodic() {
        if (topLimitSwitch.isPressed()) {
            leftMotor.stopMotor();
        } else if (bottomLimitSwitch.isPressed()) {
            leftMotor.stopMotor();
        }

        setPoint = profile.calculate(T, setPoint, goal);

        PIDController.setReference(goal.position, ControlType.kPosition, ClosedLoopSlot.kSlot0,
                feedforward.calculate(FF_VEL, FF_ACC));
    }
}
