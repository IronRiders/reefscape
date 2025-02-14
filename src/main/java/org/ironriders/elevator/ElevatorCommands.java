package org.ironriders.elevator;

import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
    
public class ElevatorCommands extends Command{
    private Level level;
    private ElevatorSubsystem elevator;
    private ElevatorSubsystem elevatorSubsystem;

    public ElevatorCommands(ElevatorSubsystem elevator){
        this.elevator = elevator;

        NamedCommands.registerCommand("Elevator rising to L1", set(Level.L1));
        NamedCommands.registerCommand("Elevator rising to L2", set(Level.L2));
        NamedCommands.registerCommand("Elevator rising to L3", set(Level.L3));
        NamedCommands.registerCommand("Elevator rising to L4", set(Level.L4));
        NamedCommands.registerCommand("Elevator going home", set(Level.Down));

        addRequirements(elevator);
    }

    public Command set(Level level){
        this.level = level;

        return elevator.run(() -> {
            elevator.setPositionInches(level.positionInches);
        })
        .until(() -> elevator.isAtPosition(level))
        .handleInterrupt(() -> elevator.stopMotor());
    }

    public Command home(){
        return elevator.runOnce(elevator::homeElevator);
    }

    @Override
    public boolean isFinished(){
        if(level == null) return false;

        return elevator.isAtPosition(level);
    }
}