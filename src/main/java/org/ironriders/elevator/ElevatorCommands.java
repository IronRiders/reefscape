package org.ironriders.elevator;

import edu.wpi.first.wpilibj2.command.Command;
import org.ironriders.elevator.ElevatorConstants.*;

public class ElevatorCommands {

    private ElevatorSubsystem elevatorSubsystem;

    public ElevatorCommands(ElevatorSubsystem elevator) {
        this.elevatorSubsystem = elevator;

        elevator.publish("Rehome", home());

        elevator.publish("Elevator to L1", set(Level.L1));
        elevator.publish("Elevator to L2", set(Level.L2));
        elevator.publish("Elevator to L3", set(Level.L3));
        elevator.publish("Elevator to L4", set(Level.L4));

        elevator.publish("Elevator to Coral Station", set(Level.CoralStation));
        elevator.publish("Elevator Down", set(Level.Down));
    }

    public Command set(ElevatorConstants.Level level) {
        return new Command() {
            public void execute() {
                elevatorSubsystem.setGoal(level);
            }

            public boolean isFinished() {
                return elevatorSubsystem.isAtPosition(level);
            }
        };
    }

    public Command home() {
        return elevatorSubsystem.defer(() -> {

            if (elevatorSubsystem.isHomed()) {
                return set(Level.Down);
            }
    
            elevatorSubsystem.reportInfo("Homing");

            return new Command() {
                public void execute() {
                    elevatorSubsystem.setMotor(-0.1);
                }

                public boolean isFinished() {
                    return elevatorSubsystem.getBottomLimitSwitch().isPressed();
                }

                public void end(boolean interrupted) {
                    elevatorSubsystem.reset();
                    elevatorSubsystem.reportHomed();
                }
            };
        });
    }

    public Command reset() {
        return elevatorSubsystem.runOnce(elevatorSubsystem::reset);
    }
}
