package org.ironriders.elevator;

import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import org.ironriders.elevator.ElevatorConstants.*;

public class ElevatorCommands {

    private ElevatorSubsystem elevatorSubsystem;

    public ElevatorCommands(ElevatorSubsystem elevator) {
        this.elevatorSubsystem = elevator;

        NamedCommands.registerCommand("Elevator rising to L1", set(Level.L1));
        NamedCommands.registerCommand("Elevator rising to L2", set(Level.L2));
        NamedCommands.registerCommand("Elevator rising to L3", set(Level.L3));
        NamedCommands.registerCommand("Elevator rising to L4", set(Level.L4));
        NamedCommands.registerCommand("Elevator going home", set(Level.Down));
        NamedCommands.registerCommand("Elevator going to Coral Station", set(Level.CoralStation));
    }

    public Command set(ElevatorConstants.Level level) {
        return elevatorSubsystem.runOnce(() -> {
            elevatorSubsystem.setPositionInches(level.positionInches);
        })
                .until(() -> elevatorSubsystem.isAtPosition(level))
                .handleInterrupt(() -> elevatorSubsystem.reset());
    }

    public Command home() {
        return elevatorSubsystem.runOnce(elevatorSubsystem::homeElevator);
    }

    public Command reset(){
        return elevatorSubsystem.runOnce(elevatorSubsystem::reset);
    }
}

    