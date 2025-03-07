package org.ironriders.elevator;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import org.ironriders.elevator.ElevatorConstants.*;

public class ElevatorCommands {

    private ElevatorSubsystem elevatorSubsystem;

    public ElevatorCommands(ElevatorSubsystem elevator) {
        this.elevatorSubsystem = elevator;

        elevator.publish("Home", home());
        elevator.publish("Elevator rising to L1", set(Level.L1));
        elevator.publish("Elevator rising to L2", set(Level.L2));
        elevator.publish("Elevator rising to L3", set(Level.L3));
        elevator.publish("Elevator rising to L4", set(Level.L4));
        elevator.publish("Elevator going home", set(Level.Down));
        elevator.publish("Elevator going to Coral Station", set(Level.CoralStation));
    }

    public Command set(ElevatorConstants.Level level) {
        return elevatorSubsystem.runOnce(() -> {
            elevatorSubsystem.setPositionInches(level.positionInches);
        })
                .until(() -> elevatorSubsystem.isAtPosition(level))
                .andThen(Commands.waitUntil(() -> { return elevatorSubsystem.isAtPosition(level); }))
                .handleInterrupt(elevatorSubsystem::reset);
    }

    public Command home() {
        // If elevator is already homed, move to home position
        if (elevatorSubsystem.isHomed()) {
            return set(Level.Down);
        }

        elevatorSubsystem.reportInfo("Homing");

        Command findHome = elevatorSubsystem.defer(
            () -> new Command() {
                public void execute() {
                    elevatorSubsystem.setMotor(-0.1);
                }

                public boolean isFinished() {
                    return elevatorSubsystem.getBottomLimitSwitch().isPressed();
                }

                public void end(boolean interrupted) {
                    elevatorSubsystem.stopMotor();
                }
            }
        );

        Command moveOffHome = elevatorSubsystem.defer(
            () -> new Command() {
                public void execute() {
                    elevatorSubsystem.setMotor(0.1);
                }

                public boolean isFinished() {
                    return !elevatorSubsystem.getBottomLimitSwitch().isPressed();
                }

                public void end(boolean interrupted) {
                    elevatorSubsystem.stopMotor();
                    elevatorSubsystem.reportHomed();
                }
            }
        );

        // Drive elevator homing
        return findHome.andThen(moveOffHome);
    }

    public Command reset(){
        return elevatorSubsystem.runOnce(elevatorSubsystem::reset);
    }
}

    