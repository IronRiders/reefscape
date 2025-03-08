package org.ironriders.targeting;

import org.ironriders.drive.DriveCommands;
import org.ironriders.lib.field.FieldElement.ElementType;
import org.ironriders.lib.field.FieldPose.Side;

import edu.wpi.first.wpilibj2.command.Command;

public class TargetingCommands {
    private TargetingSubsystem targetingSubsystem;
    private DriveCommands driveCommands;

    public TargetingCommands(TargetingSubsystem targetingSubsystem) {
        this.targetingSubsystem = targetingSubsystem;

        this.targetingSubsystem.publish("Nearest", targetNearest());

        this.targetingSubsystem.publish("Coral Station", targetNearest(ElementType.STATION));

        this.targetingSubsystem.publish("Reef", targetNearest(ElementType.REEF));
        this.targetingSubsystem.publish("Reef Left Pole", targetReefPole(Side.Left));
        this.targetingSubsystem.publish("Reef Right Pole", targetReefPole(Side.Right));
        // TODO - individual sides

        this.targetingSubsystem.publish("Processor", targetNearest(ElementType.PROCESSOR));
    }

    public Command targetStationSlot(int number) {
        return targetingSubsystem
            .runOnce(() -> {
                targetingSubsystem.setTargetSlot(number);
                targetingSubsystem.targetNearest(ElementType.STATION);
            })
            .ignoringDisable(true);
    }

    public Command targetReefPole(Side side) {

        System.out.println("Targeting " + side + " side");

        return targetingSubsystem
            .runOnce(() -> {
                targetingSubsystem.setTargetPole(side);
                targetingSubsystem.targetNearest(ElementType.REEF);
            })
            .ignoringDisable(true);
    }

    public Command targetNearest() {
        return targetingSubsystem
            .runOnce(targetingSubsystem::targetNearest)
            .ignoringDisable(true);
    }

    public Command targetNearest(ElementType type) {
        return targetingSubsystem
            .runOnce(() -> targetingSubsystem.targetNearest(type))
            .ignoringDisable(true);
    }
}
