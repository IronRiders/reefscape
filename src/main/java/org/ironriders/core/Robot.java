// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.ironriders.core;

import org.ironriders.elevator.ElevatorCommands;
import org.ironriders.elevator.ElevatorSubsystem;
import org.ironriders.coral.CoralWristCommands;
import org.ironriders.coral.CoralWristConstants.State;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The methods in this class are called automatically corresponding to each
 * mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the
 * package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {

	private final RobotContainer robotContainer;

	private Command autonomousCommand;

	public Robot() {
		robotContainer = new RobotContainer();
	}
	@Override
	public void robotPeriodic() {
		CommandScheduler.getInstance().run();
	}

	@Override
	public void autonomousInit() {
		autonomousCommand = robotContainer.getAutonomousCommand();
		robotContainer.elevatorCommands.home().schedule();

		if (autonomousCommand != null) {
			autonomousCommand.schedule();
		}
		robotContainer.coralWristCommands.reset().andThen(robotContainer.coralWristCommands.set(State.STOWED)).schedule();
	}
	@Override
	public void autonomousPeriodic() {}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when teleop starts running.
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
		}

		if (!robotContainer.elevatorSubsystem.isHomed()) {
			robotContainer.elevatorCommands.home().schedule();
		}
		if(robotContainer.elevatorSubsystem.isHomed()){
			robotContainer.elevatorCommands.reset().schedule();
		}
		robotContainer.coralWristCommands.reset().andThen(robotContainer.coralWristCommands.set(State.STOWED)).schedule();
	}

	@Override
	public void teleopPeriodic() {}

	@Override
	public void testInit() {
		CommandScheduler.getInstance().cancelAll();
	}
	@Override
	public void testPeriodic() {}

	@Override
	public void simulationInit() {}
	@Override
	public void simulationPeriodic() {}
}
