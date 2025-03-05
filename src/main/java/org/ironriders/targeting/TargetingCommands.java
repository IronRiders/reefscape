package org.ironriders.targeting;

import org.ironriders.lib.FieldElement.ElementType;
import org.ironriders.lib.FieldPose.Side;

import edu.wpi.first.wpilibj2.command.Command;

public class TargetingCommands {
    private TargetingSubsystem targetingSubsystem;

    public TargetingCommands(TargetingSubsystem targetingSubsystem) {
        this.targetingSubsystem = targetingSubsystem;

        this.targetingSubsystem.publish("Nearest", targetNearest());

        this.targetingSubsystem.publish("Coral Station", targetNearest(ElementType.STATION));
        // TODO - individual slots

        this.targetingSubsystem.publish("Reef", targetNearest(ElementType.REEF));
        this.targetingSubsystem.publish("Reef Left Pole", targetReefPole(Side.Left));
        this.targetingSubsystem.publish("Reef Right Pole", targetReefPole(Side.Right));
        // TODO - individual sides

        this.targetingSubsystem.publish("Processor", targetNearest(ElementType.PROCESSOR));
    }

    Command targetStationSlot(int number) {
        return targetingSubsystem
            .runOnce(() -> {
                targetingSubsystem.setTargetSlot(number);
                targetingSubsystem.targetNearest(ElementType.REEF);
            })
            .ignoringDisable(true);
    }

    Command targetReefPole(Side side) {
        return targetingSubsystem
            .runOnce(() -> {
                targetingSubsystem.setTargetPole(side);
                targetingSubsystem.targetNearest(ElementType.REEF);
            })
            .ignoringDisable(true);
    }

    Command targetNearest() {
        return targetingSubsystem
            .runOnce(targetingSubsystem::targetNearest)
            .ignoringDisable(true);
    }

    Command targetNearest(ElementType type) {
        return targetingSubsystem
            .runOnce(() -> targetingSubsystem.targetNearest(type))
            .ignoringDisable(true);
    }
}
