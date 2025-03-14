package org.ironriders.drive;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.ironriders.lib.GameState;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.IdealStartingState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.Waypoint;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static org.ironriders.elevator.ElevatorConstants.T;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

@Logged
public class DriveCommands {
	private final DriveSubsystem driveSubsystem;

	private final PathConstraints pathConstraints = new PathConstraints(
		DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO,
		DriveConstants.SWERVE_MAXIMUM_ACCELERATION_AUTO,
		DriveConstants.SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO,
		DriveConstants.SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO);
	

	public DriveCommands(DriveSubsystem driveSubsystem) {
		this.driveSubsystem = driveSubsystem;

		this.driveSubsystem.publish("Drive to Target", pathfindToTarget());
	}

	private LinearVelocity getVelocityMagnitude(ChassisSpeeds cs){
        return MetersPerSecond.of(new Translation2d(cs.vxMetersPerSecond, cs.vyMetersPerSecond).getNorm());
    }

	private Rotation2d getPathVelocityHeading(ChassisSpeeds cs, Pose2d target){
		if (getVelocityMagnitude(cs).in(MetersPerSecond) < 0.25) {
			var diff = target.minus(driveSubsystem.getPose()).getTranslation();
			return (diff.getNorm() < 0.01) ? target.getRotation() : diff.getAngle();//.rotateBy(Rotation2d.k180deg);
		}
		return new Rotation2d(cs.vxMetersPerSecond, cs.vyMetersPerSecond);
	}

	public Command drive(Supplier<Translation2d> translation, DoubleSupplier rotation, BooleanSupplier fieldRelative) {
		return driveSubsystem.runOnce(() -> {
			driveSubsystem.drive(translation.get(), rotation.getAsDouble(), fieldRelative.getAsBoolean());
		});
	}

	public Command driveTeleop(DoubleSupplier inputTranslationX, DoubleSupplier inputTranslationY,
			DoubleSupplier inputRotation, boolean fieldRelative) {
		if (DriverStation.isAutonomous())
			return Commands.none();

		double invert = DriverStation.getAlliance().isEmpty()
				|| DriverStation.getAlliance().get() == DriverStation.Alliance.Blue
						? 1
						: -1;

		return drive(
				() -> new Translation2d(inputTranslationX.getAsDouble(), inputTranslationY.getAsDouble())
						.times(DriveConstants.SWERVE_DRIVE_MAX_SPEED)
						.times(invert),
				() -> inputRotation.getAsDouble() * DriveConstants.SWERVE_DRIVE_MAX_SPEED * invert,
				() -> fieldRelative);
	}

	public Command jog(double robotRelativeAngleDegrees) {
		// Note - PathFinder does not do well with small moves so we move manually

		// Compute distance to travel (TODO - distance is slightly fictional without PID
		// control)
		var distance = Units.inchesToMeters(DriveConstants.JOG_DISTANCE_INCHES);

		// Compute velocity
		var vector = new Translation2d(
				distance,
				Rotation2d.fromDegrees(robotRelativeAngleDegrees));
		var scale = Math.max(Math.abs(vector.getX()), Math.abs(vector.getY())) / DriveConstants.JOG_SPEED;
		var velocity = vector.div(scale);

		return driveSubsystem.runOnce(() -> {
			var startPosition = driveSubsystem.getPose().getTranslation();

			driveTeleop(velocity::getX, velocity::getY, () -> 0, false)
					.repeatedly()
					.until(() -> driveSubsystem.getPose().getTranslation().getDistance(startPosition) > distance)
					.schedule();
		});
	}

	public Command pathfindToPose(Pose2d targetPose) {
		return driveSubsystem.defer(() -> {
			List<Waypoint> waypoints = PathPlannerPath.waypointsFromPoses(
            	new Pose2d(driveSubsystem.getPose().getTranslation(), getPathVelocityHeading(driveSubsystem.getSwerveDrive().getFieldVelocity(), targetPose))
        	);

			PathPlannerPath path = new PathPlannerPath(
            	waypoints, 
            	pathConstraints,
            	new IdealStartingState(getVelocityMagnitude(driveSubsystem.getSwerveDrive().getFieldVelocity()), driveSubsystem.getSwerveDrive().getPose().getRotation()), 
            	new GoalEndState(0.0, targetPose.getRotation())
        	);

			return (AutoBuilder.followPath(path)); // TODO: implement pid align

			//driveSubsystem.pathfindCommand = AutoBuilder.pathfindToPose(targetPose, new PathConstraints(
			//		DriveConstants.SWERVE_MAXIMUM_SPEED_AUTO,
			//		DriveConstants.SWERVE_MAXIMUM_ACCELERATION_AUTO,
			//		DriveConstants.SWERVE_MAXIMUM_ANGULAR_VELOCITY_AUTO,
			//		DriveConstants.SWERVE_MAXIMUM_ANGULAR_ACCELERATION_AUTO));
			//return driveSubsystem.pathfindCommand;
		});
	}

	public Command pathfindToTarget() {
		return driveSubsystem.defer(() -> {
			var pose = GameState.getTargetRobotPose();
			if (pose.isEmpty()) {
				return Commands.none();
			}

			return pathfindToPose(pose.get().toPose2d());
		});
	}

	public Command cancelPathfind() {
		return driveSubsystem.runOnce(() -> {
			if (driveSubsystem.pathfindCommand != null) {
				driveSubsystem.pathfindCommand.cancel();
			}
		});
	}

	public Command setDriveTrainSpeed(boolean slow){
		return driveSubsystem.runOnce(() -> {
			driveSubsystem.setSpeed(slow);
		});
	}
}
