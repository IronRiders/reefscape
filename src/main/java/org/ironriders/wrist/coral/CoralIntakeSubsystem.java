package org.ironriders.wrist.coral;

import static org.ironriders.wrist.coral.CoralIntakeConstants.CORAL_INTAKE_CURRENT_STALL_LIMIT;
import static org.ironriders.wrist.coral.CoralIntakeConstants.CORAL_INTAKE_MOTOR;

import org.ironriders.lib.IronSubsystem;
import org.ironriders.wrist.coral.CoralIntakeConstants.CoralIntakeState;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

public class CoralIntakeSubsystem extends IronSubsystem {

    private final CoralIntakeCommands commands;

    private final SparkMax coralMotor = new SparkMax(CORAL_INTAKE_MOTOR, MotorType.kBrushless);
    private final SparkMaxConfig coralMotorConfig = new SparkMaxConfig();

    private final SparkLimitSwitch beamBreak = coralMotor.getForwardLimitSwitch();

    public CoralIntakeSubsystem() {
        coralMotorConfig
                .smartCurrentLimit(CORAL_INTAKE_CURRENT_STALL_LIMIT)
                .inverted(true)
                .idleMode(IdleMode.kBrake);
        coralMotor.configure(coralMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        commands = new CoralIntakeCommands(this);
    }

    @Override
    public void periodic() {
        publish("Velocity", coralMotor.getEncoder().getVelocity());
        publish("Limit Switch Triggered", getLimitSwitchTriggered());
    }

    public void set(CoralIntakeState state) {
        coralMotor.set(state.getSpeed());

        publish("Set State", state.name());
    }

    public boolean getLimitSwitchTriggered() {
        return beamBreak.isPressed();
    }

    public CoralIntakeCommands getCommands() {
        return commands;
    }
}
