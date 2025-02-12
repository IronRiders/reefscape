package org.ironriders.elevator;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class ElevatorCommands {

    private ElevatorSubsystem elevatorSubsystem;

    public ElevatorCommands(ElevatorSubsystem elevatorSubsystem) {
        this.elevatorSubsystem = elevatorSubsystem;
    }

    public Command setLevel(ElevatorConstants.Level level) {
        return elevatorSubsystem.runOnce(() -> {
            elevatorSubsystem.setGoal(level);
        }).andThen(Commands.waitUntil(() -> elevatorSubsystem.atGoal()));
    }
}
