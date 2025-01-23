package org.ironriders.manipulator;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkMaxAlternateEncoder;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import org.ironriders.manipulator.ManipulatorConstants.*;

/**
 * The ManipulatorSubsystem controls the intake and placement of coral and algae.
 * This subsystem is SEPERATE from the ElevatorSubsystem, which controls the (vertical) position of the manipulator.
 */
public class ManipulatorSubsystem {

    private ManipulatorCommands commands;

    // Motors & such
    private SparkMax coralWrist;
    private AbsoluteEncoder coralWristEncoder;
    private SparkMax coralIntake;

    private SparkMax algaeMotorOne;
    private SparkMax algaeMotorTwo;

    public ManipulatorSubsystem() {
        commands = new ManipulatorCommands();

        // initialize motors
        coralWrist = new SparkMax(ManipulatorConstants.CORAL_WRIST_CAN_ID, MotorType.kBrushless);
        coralWristEncoder = coralWrist.getAbsoluteEncoder();
        coralIntake = new SparkMax(ManipulatorConstants.CORAL_INTAKE_CAN_ID, MotorType.kBrushless);
        algaeMotorOne = new SparkMax(ManipulatorConstants.ALGAE_MOTOR_ONE_CAN_ID, MotorType.kBrushless);
        algaeMotorTwo = new SparkMax(ManipulatorConstants.ALGAE_MOTOR_TWO_CAN_ID, MotorType.kBrushless);

        // set up the wrist motor
        SparkMaxConfig coralWristConfig = new SparkMaxConfig();
        coralWristConfig
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(40)
            .closedLoop
                .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
                .pidf(0.55, 0, 0, 0.00375);
        coralWrist.configure(coralWristConfig, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);

        // set up other motors
        SparkMaxConfig otherMotorsConfig = new SparkMaxConfig();
        otherMotorsConfig
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(15);
        coralIntake.configure(otherMotorsConfig, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);
        algaeMotorOne.configure(otherMotorsConfig, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);
        algaeMotorTwo.configure(otherMotorsConfig, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);
    }

    public ManipulatorCommands getCommands() {
        return commands;
    }
}
