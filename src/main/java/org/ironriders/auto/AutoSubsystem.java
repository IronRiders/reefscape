package org.ironriders.auto;

import org.ironriders.lib.IronSubsystem;

public class AutoSubsystem extends IronSubsystem {
    private AutoCommands commands;

    public AutoSubsystem() {
        publish("Test", "null");
    }

    @Override
    public void periodic() {

    }

    public AutoCommands getCommands() {
        return commands;
    }
}
