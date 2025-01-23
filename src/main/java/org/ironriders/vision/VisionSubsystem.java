package org.ironriders.vision;

import java.util.List;

import org.ironriders.drive.DriveSubsystem;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class VisionSubsystem extends SubsystemBase {
    private VisionCommands commands = new VisionCommands(this, new DriveSubsystem());
    private PhotonCamera camera = new PhotonCamera(VisionConstants.CAM_NAME);
    public boolean canAlignCoral;//you can get this if you want!
    /** Fetch the VisionCommands instance */
    public VisionCommands getCommands() {
        return commands;
    }

    public PhotonCamera getCamera() {
        return this.camera;
    }

    public void alignwithCoral() {
        commands.alignCoral(camera);
    }

    @Override
    public void periodic() {
        PhotonPipelineResult result = camera.getLatestResult();
        if (!result.hasTargets()) {
            SmartDashboard.putString("IDs", "None");
            return;
        }
        List<PhotonTrackedTarget> targets = result.getTargets();
        String ids = "IDs: ";
        for (PhotonTrackedTarget target : targets) {
            ids += (target.fiducialId) + " ";
        }
        SmartDashboard.putString("IDs", ids);

        int[] tags = null;
        if (DriverStation.getAlliance().get() == Alliance.Red) {
            tags = VisionConstants.REEF_TAG_IDS_RED;

        } else {
            tags = VisionConstants.REEF_TAG_IDS_BLUE;
        }
        boolean foundTag=false;
        for (int i : tags) {
            for (PhotonTrackedTarget target : targets) {//nested for loops feel slow but also i don't really care
                if(i==target.fiducialId){
                    foundTag=true;
                }
            }
        }
        canAlignCoral=foundTag;
        SmartDashboard.putBoolean("Can Align Coral", canAlignCoral);
    }
}
