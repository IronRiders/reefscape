package org.ironriders.wrist.algae;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.ResetMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static org.ironriders.wrist.algae.AlgaeIntakeConstants.*;

import org.ironriders.lib.IronSubsystem;
import org.ironriders.wrist.algae.AlgaeIntakeConstants.AlgaeIntakeState;

public class AlgaeIntakeSubsystem extends IronSubsystem {

    private final AlgaeIntakeCommands commands;

    // find actual motor IDs
    private final SparkMax algaeLeftMotor = new SparkMax(ALGAELEFTINTAKEMOTOR, MotorType.kBrushless);
    private final SparkMax algaeRightMotor = new SparkMax(ALGAERIGHTINTAKEMOTOR, MotorType.kBrushless);
    private final SparkMaxConfig algaeMotorConfig = new SparkMaxConfig();


    @SuppressWarnings("unused")
    private boolean hasAlgae = false;

    public AlgaeIntakeSubsystem() {
        algaeMotorConfig
                .smartCurrentLimit(ALGAE_INTAKE_CURRENT_STALL_LIMIT)
                .inverted(false)
                .idleMode(IdleMode.kCoast);
        algaeLeftMotor.configure(algaeMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        algaeRightMotor.configure(algaeMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        commands = new AlgaeIntakeCommands(this);

    }

    @Override
    public void periodic() {

        publish("Velocity", getSpeed());
        publish("Forward Left Motor Current", algaeLeftMotor.getOutputCurrent());
        publish("Forward Right Motor Current", algaeRightMotor.getOutputCurrent());
    }

    public void setHasAlgae(boolean hasAlgae) {
        this.hasAlgae = hasAlgae;
    }

    public void set(AlgaeIntakeState state) {
        algaeLeftMotor.set(state.getSpeed());
        algaeRightMotor.set(-state.getSpeed());

        SmartDashboard.putString(DASHBOARD_PREFIX_ALGAE + "state", state.name());
    }

    private double getSpeed() {
        return algaeLeftMotor.getEncoder().getVelocity();
    }

    public boolean getLimitSwitchTriggered() {
        // return limitSwitch.isPressed(); //hasn't been put on the robot
        return false;
    }

    public void reset() {
        set(AlgaeIntakeState.STOP);
    }

    public AlgaeIntakeCommands getCommands() {
        return commands;
    }

}
