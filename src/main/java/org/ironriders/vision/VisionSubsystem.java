package org.ironriders.vision;


import org.ironriders.drive.DriveSubsystem;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {
    private VisionCommands commands = new VisionCommands(this, new DriveSubsystem());
    /** Fetch the VisionCommands instance */
    public VisionCommands getCommands() {
        return commands;
    }
    
    public void alignwithCoral(){
        commands.alignCoral();
    }
}
