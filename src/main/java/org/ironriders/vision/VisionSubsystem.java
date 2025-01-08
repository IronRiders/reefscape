package org.ironriders.vision;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {
    private VisionCommands commands;

    public VisionCommands getCommands() {
        return commands;
    }
}
