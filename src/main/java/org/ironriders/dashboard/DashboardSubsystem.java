package org.ironriders.dashboard;

import edu.wpi.first.net.WebServer;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Dashboard management.
 * 
 * We use Elastic as our dashboard.  You can load/save the configuration in deploy/elastic-layout.json.
 * 
 * Drive computers must be configured as follows:
 * 
 *   - Edit c:\Users\Public\Public Documents\FRC\FRC DS Data Storage.txt
 *   - Set the following values:
 *       DashboardType = 0    
 *       DashboardCmdLine = "wscript C:\\Users\\Public\\wpilib\\2025\\tools\\Elastic.vbs"
 *   - Restart DriverStation
 *   - After Elastic starts choose File -> Download From Robot to load deployed configuration
 */
public class DashboardSubsystem extends SubsystemBase {
    final PowerDistribution pdh = new PowerDistribution();

    public DashboardSubsystem() {
        // Elastic loads configuration from the robot using this web server
        WebServer.start(5800, Filesystem.getDeployDirectory().getPath());

        // Install PDH into dashboard (causes CAN error, need to investigate)
        //SmartDashboard.putData("General/PDH", pdh);
    }
}
