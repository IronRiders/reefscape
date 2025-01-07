package org.ironriders.vision;

import java.util.List;

import org.ironriders.vision.VisionSubsystem;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj2.command.Command;
import org.photonvision.*;
import org.photonvision.targeting.PhotonTrackedTarget;

public class VisionCommands {
    private final VisionSubsystem VisionSubsystem;
    PhotonCamera camera = new PhotonCamera(VisionConstants.CAM_NAME);
    AprilTagFieldLayout aprilTagFieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

    public VisionCommands(VisionSubsystem visionSubsystem) {
		this.VisionSubsystem = visionSubsystem;
	}
    public Command alignCoral(){
        return VisionSubsystem.runOnce(()->{

        }

        );
    }

    private Transform3d getPathToTag(int id){
        var result = camera.getLatestResult();
        boolean hasTargets = result.hasTargets();
        if(!hasTargets){
            return new Transform3d();
        }
        List<PhotonTrackedTarget> targets = result.getTargets();
        for (PhotonTrackedTarget target : targets) {
            if(target.getFiducialId()==id){
                double poseAmbiguity = target.getPoseAmbiguity();
                Transform3d pose = target.getBestCameraToTarget();
                return pose;
            }
        }
                return new Transform3d();
    }
}
