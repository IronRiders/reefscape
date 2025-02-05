package org.ironriders.elevator;

import edu.wpi.first.wpilibj2.command.Command;

public class ElevatorCommands extends Command {
    private Level level;
    private ElevatorSubsystem elevator;

    public ElevatorCommands(Level level, ElevatorSubsystem elevator){
        this.level = level;
        this.elevator = elevator;

        addRequirements(elevator);
    }

    @Override
    public void execute(){
        elevator.setGoal(level);
    }
}
