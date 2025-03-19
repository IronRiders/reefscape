package org.ironriders.Manipulators.Algae.AlgaeIntake;

import static org.ironriders.Manipulators.Algae.AlgaeIntake.AlgaeIntakeConstants.ALGAE_INTAKE_CURRENT_STALL_LIMIT;
import static org.ironriders.Manipulators.Algae.AlgaeIntake.AlgaeIntakeConstants.ALGAE_LEFT_ID;
import static org.ironriders.Manipulators.Algae.AlgaeIntake.AlgaeIntakeConstants.ALGAE_RIGHT_ID;

import org.ironriders.lib.IronSubsystem;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

public class AlgaeIntakeSubsystem extends IronSubsystem {

    private final SparkMax algaeLeftMotor = new SparkMax(ALGAE_LEFT_ID, MotorType.kBrushless);
    private final SparkMax algaeRightMotor = new SparkMax(ALGAE_RIGHT_ID, MotorType.kBrushless);
    private final SparkMaxConfig algaeMotorConfig = new SparkMaxConfig();
    
    private final CommandJoystick secondaryController;
    private boolean hasAlgae = false;

    public AlgaeIntakeSubsystem(CommandJoystick secondaryController) {
        this.secondaryController = secondaryController;

        algaeMotorConfig
                .smartCurrentLimit(ALGAE_INTAKE_CURRENT_STALL_LIMIT)
                .inverted(false)
                .idleMode(IdleMode.kCoast);
        algaeLeftMotor.configure(algaeMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        algaeRightMotor.configure(algaeMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    @Override
    public void periodic() {
        publish("Velocity", getSpeed());
        publish("Forward Left Motor Current", algaeLeftMotor.getOutputCurrent());
        publish("Forward Right Motor Current", algaeRightMotor.getOutputCurrent());
        publish("Joystick Input", secondaryController.getY());
    }

    public void setHasAlgae(boolean hasAlgae) {
        this.hasAlgae = hasAlgae;
    }

    /**
     * Set motor speeds dynamically using joystick input.
     */
    public void setFromJoystick() {
        double joystickY = secondaryController.getY();

        // Apply deadband to prevent unwanted movement
        if (Math.abs(joystickY) < 0.05) {
            joystickY = 0;
        }

        // Apply a speed coefficient (adjust this value as needed)
        double motorSpeed = joystickY * 0.8;

        // Clamp speed to prevent exceeding safe limits
        motorSpeed = Math.max(-1.0, Math.min(motorSpeed, 1.0));

        // Set motor speeds
        algaeLeftMotor.set(motorSpeed);
        algaeRightMotor.set(-motorSpeed);

        publish("Motor Speed", motorSpeed);
    }

    private double getSpeed() {
        return algaeLeftMotor.getEncoder().getVelocity();
    }

    public boolean getLimitSwitchTriggered() {
        // return limitSwitch.isPressed(); // hasn't been put on the robot
        return false;
    }

    public void reset() {
        set(0.0);
    }

    /**
     * Allows manual setting of motor speed (useful for testing).
     */
    public void set(double speed) {
        algaeLeftMotor.set(speed);
        algaeRightMotor.set(-speed);
        publish("Manual Speed", speed);
    }
}
