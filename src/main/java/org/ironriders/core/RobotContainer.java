package org.ironriders.Core;

import static org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants.FORWARD_LIMIT;

import org.ironriders.Drive.DriveCommands;
import org.ironriders.Drive.DriveConstants;
import org.ironriders.Drive.DriveSubsystem;
import org.ironriders.Manipulators.Coral.CoralIntake.CoralIntakeCommands;
import org.ironriders.Manipulators.Coral.CoralIntake.CoralIntakeConstants;
import org.ironriders.Manipulators.Coral.CoralIntake.CoralIntakeSubsystem;
import org.ironriders.Manipulators.Coral.CoralWrist.CoralWristCommands;
import org.ironriders.Manipulators.Coral.CoralWrist.CoralWristSubsystem;
import org.ironriders.Manipulators.Coral.CoralWrist.CoralWristConstants;
import org.ironriders.Manipulators.Elevator.ElevatorCommands;
import org.ironriders.Manipulators.Elevator.ElevatorConstants;
import org.ironriders.Manipulators.Elevator.ElevatorSubsystem;
import org.ironriders.Manipulators.Algae.AlgaeIntake.AlgaeIntakeCommands;
import org.ironriders.Manipulators.Algae.AlgaeIntake.AlgaeIntakeConstants;
import org.ironriders.Manipulators.Algae.AlgaeIntake.AlgaeIntakeSubsystem;
import org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristCommands;
import org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristSubsystem;
import org.ironriders.Manipulators.Algae.AlgaeWrist.AlgaeWristConstants;
import org.ironriders.climb.ClimbCommands;
import org.ironriders.climb.ClimbConstants;
import org.ironriders.climb.ClimbSubsystem;
import org.ironriders.targeting.TargetingCommands;
import org.ironriders.targeting.TargetingSubsystem;
import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class RobotContainer {

    // Controllers
    private final CommandXboxController primaryController = new CommandXboxController(DriveConstants.PRIMARY_CONTROLLER_PORT);
    private final CommandJoystick secondaryController = new CommandJoystick(DriveConstants.OPERATOR_PORT);

    // Subsystems
    public final DriveSubsystem driveSubsystem = new DriveSubsystem();
    public final DriveCommands driveCommands = driveSubsystem.getCommands();

    public final TargetingSubsystem targetingSubsystem = new TargetingSubsystem();
    public final TargetingCommands targetingCommands = targetingSubsystem.getCommands();

    public final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    public final ElevatorCommands elevatorCommands = elevatorSubsystem.getCommands();

    public final CoralWristSubsystem coralWristSubsystem = new CoralWristSubsystem();
    public final CoralWristCommands coralWristCommands = coralWristSubsystem.getCommands();

    public final CoralIntakeSubsystem coralIntakeSubsystem = new CoralIntakeSubsystem();
    public final CoralIntakeCommands coralIntakeCommands = coralIntakeSubsystem.getCommands();

    public final AlgaeWristSubsystem algaeWristSubsystem = new AlgaeWristSubsystem();
    public final AlgaeWristCommands algaeWristCommands = algaeWristSubsystem.getCommands();

    public final AlgaeIntakeSubsystem algaeIntakeSubsystem = new AlgaeIntakeSubsystem(secondaryController);
    public final AlgaeIntakeCommands algaeIntakeCommands = new AlgaeIntakeCommands(algaeIntakeSubsystem, secondaryController);

    public final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
    public final ClimbCommands climbCommands = climbSubsystem.getCommands();

    private final SendableChooser<Command> autoChooser;

    public final RobotCommands robotCommands = new RobotCommands(
        driveCommands, targetingCommands, elevatorCommands,
        coralWristCommands, coralIntakeCommands,
        algaeWristCommands, algaeIntakeCommands,
        climbCommands,
        primaryController.getHID(),
        secondaryController
    );


    public RobotContainer() {
        // Configure the trigger bindings
        configureBindings();

        // Auto chooser
        autoChooser = AutoBuilder.buildAutoChooser();
        SmartDashboard.putData("Auto Select", autoChooser);
    }

    private void configureBindings() {

        // DRIVE CONTROLS
        driveSubsystem.setDefaultCommand(
                robotCommands.driveTeleop(
                        () -> primaryController.getLeftY(),
                        () -> primaryController.getLeftX(),
                        () -> primaryController.getRightX()
                ));

        // slows down drivetrain when pressed
        primaryController.leftTrigger()
                .onTrue(driveCommands.setDriveTrainSpeed(true))
                .onFalse(driveCommands.setDriveTrainSpeed(false));

        // jog commands on POV buttons
        for (var angle = 0; angle < 360; angle += 45) {
            primaryController.pov(angle).onTrue(driveCommands.jog(-angle));
        }

        // Joystick Y-axis controls algae intake dynamically
        secondaryController.axisGreaterThan(1, 0.05)
            .whileTrue(algaeIntakeCommands.runIntakeWithJoystick());

        // Secondary Driver left-side buttons
        secondaryController.button(1)
                .whileTrue(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.EJECT))
                .whileFalse(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.STOP));

        secondaryController.button(2)
                .whileTrue(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.GRAB))
                .whileFalse(coralIntakeCommands.set(CoralIntakeConstants.CoralIntakeState.STOP));

        secondaryController.button(5).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L1));
        secondaryController.button(6).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L2));
        secondaryController.button(7).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L3));
        secondaryController.button(8).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.L4));
        secondaryController.button(9).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.CoralStation));
        secondaryController.button(10).onTrue(robotCommands.moveElevatorAndWrist(ElevatorConstants.Level.Down));

        // Right-side buttons
        secondaryController.button(4).onTrue(algaeWristCommands.set(AlgaeWristConstants.AlgaeWristState.EXTENDED));
        secondaryController.button(3).onTrue(algaeWristCommands.set(AlgaeWristConstants.AlgaeWristState.STOWED));
        secondaryController.button(13).onTrue(climbCommands.goTo(ClimbConstants.Targets.MAX));
        secondaryController.button(14).onTrue(climbCommands.goTo(ClimbConstants.Targets.TARGET));
    }

    /**
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
