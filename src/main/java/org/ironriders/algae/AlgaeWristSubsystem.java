package org.ironriders.algae;

import com.revrobotics.spark.SparkLimitSwitch;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.LimitSwitchConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.ResetMode;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static org.ironriders.algae.AlgaeWristConstants.*;

import org.ironriders.algae.AlgaeWristCommands;
import org.ironriders.elevator.ElevatorConstants;
import org.ironriders.lib.RobotUtils;

public class AlgaeWristSubsystem extends SubsystemBase {
    // Why do we extend subsystem base?
    private final AlgaeWristCommands commands;

    // find acutal motor IDs
    private final SparkMax motor = new SparkMax(ALGAEWRISTMOTOR, MotorType.kBrushless);
    private final PIDController pidController = new PIDController(P, I, D);
    private  RelativeEncoder encoder = motor.getEncoder();
    private final SparkLimitSwitch forwardLimitSwitch = motor.getForwardLimitSwitch();
    private final SparkLimitSwitch reverseLimitSwitch = motor.getReverseLimitSwitch();
    private final LimitSwitchConfig forwardLimitSwitchConfig = new LimitSwitchConfig();
    private final LimitSwitchConfig reverseLimitSwitchConfig = new LimitSwitchConfig();
    private final SparkMaxConfig motorConfig = new SparkMaxConfig();
     private TrapezoidProfile.State goalState = new TrapezoidProfile.State();
    private TrapezoidProfile.State setPointState = new TrapezoidProfile.State();
    private TrapezoidProfile profile;
    private boolean isHomed = false;

    public AlgaeWristSubsystem() {
        SmartDashboard.putNumber("Algae P", AlgaeWristConstants.P);
        SmartDashboard.putNumber("Algae I", AlgaeWristConstants.I);
        SmartDashboard.putNumber("Algae D", AlgaeWristConstants.D);

        TrapezoidProfile.Constraints constraints = new TrapezoidProfile.Constraints(MAX_VEL, MAX_ACC);
        profile = new TrapezoidProfile(constraints);
        forwardLimitSwitchConfig.forwardLimitSwitchEnabled(true)
                .forwardLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // this sets allows the limit switch to
                                                                                 // disable the motor
        reverseLimitSwitchConfig.reverseLimitSwitchEnabled(true)
                .reverseLimitSwitchType(LimitSwitchConfig.Type.kNormallyClosed); // It also sets the Type to k normally
                                                                                 // closed see
                                                                                 // https://docs.revrobotics.com/brushless/spark-max/specs/data-port#limit-switch-operation
        motorConfig
                .smartCurrentLimit(ALGAE_WRIST_CURRENT_STALL_LIMIT)

                .idleMode(IdleMode.kCoast)
                .apply(forwardLimitSwitchConfig)
                .apply(reverseLimitSwitchConfig);

        motor.configure(motorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        setGoal(getRotation());

        pidController.setTolerance(ALGAE_WRIST_TOLERENCE);

        commands = new AlgaeWristCommands(this);
    }

    @Override
    public void periodic() {

        pidController.setP(SmartDashboard.getNumber("Algae P", AlgaeWristConstants.P));
        pidController.setI(SmartDashboard.getNumber("Algae I", AlgaeWristConstants.I));
        pidController.setD(SmartDashboard.getNumber("Algae D", AlgaeWristConstants.D));

        System.out.println("P: " + pidController.getP() + " I: " + pidController.getI() + " D:" + pidController.getD());

        setPointState = profile.calculate(t, setPointState, goalState);

        SmartDashboard.putNumber("Coral Wrist Set Postion", setPointState.position);
        double output = pidController.calculate(getRotation(),setPointState.position);
        if(motor.getReverseLimitSwitch().isPressed()){
            handleBottomLimitSwitch();
        }
        if(isHomed){
            motor.set(output);
            
        }
        
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "Homed", isHomed);
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "rotation", getRotation());
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "encoder", motor.getEncoder().getPosition());
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "encoder converted",getRotation() );
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "output", output);
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "setPoint", goalState.position);
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "fowardSwitch", forwardLimitSwitch.isPressed());
        SmartDashboard.putBoolean(DASHBOARD_PREFIX + "reverseSwitch", reverseLimitSwitch.isPressed());
        SmartDashboard.putNumber(DASHBOARD_PREFIX + "motor current", motor.getOutputCurrent());
    }

    public void setGoal(double position) {
        goalState = new TrapezoidProfile.State(MathUtil.clamp(position, MIN_POSITION, MAX_POSITION), 0);
    }

    public void reset() {
        goalState = new TrapezoidProfile.State(setPointState.position, 0);
        pidController.reset();
    }

    private double getRotation() {
        // System.out.println(encoder.getPosition() * 360);
        return encoder.getPosition() * 360 * GEAR_RATIO * SPROCKET_RATIO;
    }

    private void handleBottomLimitSwitch() {
        // System.out.println("SET TO ZERO");
        encoder.setPosition(0);
        isHomed = true;
    }

    public boolean atPosition() {
        return pidController.atSetpoint();
    }

    public AlgaeWristCommands getCommands() {
        return commands;
    }

}
