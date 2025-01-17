// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveConstants;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.vision.VisionCommands;
import org.ironriders.vision.VisionSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final DriveSubsystem driveSubsystem = new DriveSubsystem();
  private final DriveCommands driveCommands = driveSubsystem.getCommands();

  private final VisionSubsystem visionSubsystem = new VisionSubsystem();
  private final VisionCommands visionCommands = visionSubsystem.getCommands();

  private final SendableChooser<Command> autoChooser;
  private final CommandXboxController primaryController =
      new CommandXboxController(DriveConstants.PRIMARY_CONTROLLER_PORT);
  
  private final CommandGenericHID secondaryController =
      new CommandGenericHID(DriveConstants.SECONDARY_CONTROLLER_PORT);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();

    // Init auto chooser
    // autoChooser = AutoBuilder.buildAutoChooser();
    // SmartDashboard.putData("Auto Select", autoChooser);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
   * an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link
   * CommandXboxController
   * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {

    secondaryController.button(1).onTrue(driveCommands.runPath("Example Path"));
    //secondaryController.button(2).onTrue(driveCommands.runPath("NAMEME"));


    driveSubsystem.setDefaultCommand(
        driveCommands.driveTeleop(
            () -> Utils.controlCurve(
              primaryController.getLeftX(), 
              DriveConstants.TRANSLATION_CONTROL_EXPONENT, 
              DriveConstants.TRANSLATION_CONTROL_DEADBAND),
            () -> Utils.controlCurve(
              primaryController.getLeftY(), 
              DriveConstants.TRANSLATION_CONTROL_EXPONENT, 
              DriveConstants.TRANSLATION_CONTROL_DEADBAND),
            () -> Utils.controlCurve(
              primaryController.getLeftX(), 
              DriveConstants.ROTATION_CONTROL_EXPONENT, 
              DriveConstants.ROTATION_CONTROL_DEADBAND)
        )
        
    );
    primaryController.a().onTrue(driveCommands.driveTest());
    primaryController.a().onTrue(visionCommands.alignCoral());
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous. THIS IS A PLACEHOLDER!
    // return autoChooser.getSelected();
    return new Command() {

    };
  }
  
}
