package org.ironriders.climb;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import static org.ironriders.algae.AlgaeIntakeConstants.*;

public class ClimbSubsystem extends SubsystemBase {

    public final SparkMaxConfig climbMotorConfig = new SparkMaxConfig();
    // finds the motor for the climber
    private final SparkMax climbMotor = new SparkMax(ClimbConstants.CLIMBER_MOTOR, SparkLowLevel.MotorType.kBrushless); // TODO: ask if it's actually brushless
    private final ClimbCommands commands;
    public ClimbSubsystem() {
        climbMotorConfig // if there's an issue with breaks and stuff, the docs show that limit switch is enabled by default
                .smartCurrentLimit(12)
                // .voltageCompensation(ALGAE_INTAKE_COMPENSATED_VOLTAGE)
                .idleMode(IdleMode.kBrake);
        climbMotor.configure(climbMotorConfig,ResetMode.kResetSafeParameters,PersistMode.kPersistParameters);
        // neo 1.1 for the motor

        commands = new ClimbCommands(this);
    }

    public void set(ClimbConstants.State state) {
        climbMotor.set(state.getSpeed());
    }

    //gets signaled to pull
    //pulls

    //zeros, then set a soft limit

}
