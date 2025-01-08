package org.ironriders.vision;


import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {
    private VisionCommands commands;
    /** Fetch the VisionCommands instance */
    public VisionCommands getCommands() {
        return commands;
    }
    
    public void alignwithCoral(){
        commands.alignCoral();
    }
}
