package org.ironriders.lib;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Common base for 4180 subsystems.
 */
public abstract class IronSubsystem extends SubsystemBase {
    private final String diagnosticName = this.getClass().getSimpleName().replaceAll("Subsystem$", "");
    private final String dashboardPrefix = "Subsystems/" + diagnosticName + "/";
    private final String messagePrefix = diagnosticName + ": ";

    public double getDiagnostic(String name, double defaultValue) {
        return SmartDashboard.getNumber(name, defaultValue);
    }
    
    public void addDiagnostic(String name, boolean value) {
        SmartDashboard.putBoolean(dashboardPrefix + name, value);
    }

    public void addDiagnostic(String name, double value) {
        SmartDashboard.putNumber(dashboardPrefix + name, value);
    }

    public void addDiagnostic(String name, String value) {
        SmartDashboard.putString(dashboardPrefix + name, value);
    }

    public void reportInfo(String message) {
        // TODO - figure out how to create green messages
        System.out.println(messagePrefix + message);
    }

    public void reportError(String message) {
        DriverStation.reportError(messagePrefix + message, false);
    }

    public void reportWarning(String message) {
        DriverStation.reportWarning(messagePrefix + message, false);
    }
}
