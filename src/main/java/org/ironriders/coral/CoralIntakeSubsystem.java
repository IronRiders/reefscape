package org.ironriders.coral;

import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static org.ironriders.coral.CoralIntakeConstants.*;

import org.ironriders.coral.CoralIntakeConstants.State;

public class CoralIntakeSubsystem extends SubsystemBase {

    private final CoralIntakeCommands commands;

    private final SparkMax coralMotor = new SparkMax(CORAL_INTAKE_MOTOR, MotorType.kBrushless);
    private final SparkMaxConfig coralMotorConfig = new SparkMaxConfig();

    private final SparkLimitSwitch beamBreak = coralMotor.getForwardLimitSwitch();

    private boolean hasCoral = false;

    public CoralIntakeSubsystem() {
        coralMotorConfig
                .smartCurrentLimit(CORAL_INTAKE_CURRENT_STALL_LIMIT)
                .voltageCompensation(CORAL_INTAKE_COMPENSATED_VOLTAGE)
                .idleMode(IdleMode.kBrake);
        coralMotor.configure(coralMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        commands = new CoralIntakeCommands(this);

    }

    @Override
    public void periodic() {

        SmartDashboard.putNumber(DASHBOARD_PREFIX_CORAL + "velocity", getSpeed());

    }

    public void setHasAlgae(boolean hasAlgae) {
        this.hasCoral = hasAlgae;
    }

    public void set(State state) {
        coralMotor.set(state.getSpeed());

        SmartDashboard.putString(DASHBOARD_PREFIX_CORAL + "state", state.name());
    }

    private double getSpeed() {
        return coralMotor.getEncoder().getVelocity();
    }

    public boolean getLimitSwitchTriggered() {
        return beamBreak.isPressed();
    }

    public void reset() {
        set(State.STOP);
    }

    public CoralIntakeCommands getCommands() {
        return commands;
    }

}
