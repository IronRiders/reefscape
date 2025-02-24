package org.ironriders.drive;

import java.io.IOException;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.RobotConfig;
import swervelib.SwerveDrive;
import swervelib.parser.SwerveParser;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.ironriders.vision.Vision;

/**
 * The DriveSubsystem encompasses everything that the Swerve Drive needs to
 * function. It keeps track of the robot's position and angle, and uses the controller
 * input to figure out how the individual modules need to turn and be angled.
 */
public class DriveSubsystem extends SubsystemBase {

	private DriveCommands commands;

	private SwerveDrive swerveDrive;
	private Vision vision;

	public DriveSubsystem() throws RuntimeException {
		try {
			swerveDrive = new SwerveParser(DriveConstants.SWERVE_JSON_DIRECTORY) // YAGSL reads from the deply/swerve
																					// directory.
					.createSwerveDrive(DriveConstants.SWERVE_MAXIMUM_SPEED_TELEOP);
		} catch (IOException e) { // instancing SwerveDrive can throw an error, so we need to catch that.
			throw new RuntimeException("Error configuring swerve drive", e);
		}

		commands = new DriveCommands(this);
		this.vision = new Vision();

		swerveDrive.setHeadingCorrection(false);
		SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;

		RobotConfig robotConfig = null;
		try {
			robotConfig = RobotConfig.fromGUISettings();
		} catch (Exception e) {
			throw new RuntimeException("Could not load path planner config", e);
		}

		AutoBuilder.configure(
				swerveDrive::getPose,
				swerveDrive::resetOdometry,
				swerveDrive::getRobotVelocity,
				(speeds, feedforwards) -> swerveDrive.setChassisSpeeds(speeds),
				DriveConstants.HOLONOMIC_CONFIG,
				robotConfig,
				() -> {
					var alliance = DriverStation.getAlliance();
					if (alliance.isPresent()) {
						return alliance.get() == DriverStation.Alliance.Red;
					}
					return false;
				},
				this);
	}

	@Override
	public void periodic() {
		vision.addPoseEstimates(swerveDrive);
	}

	/**
	 * Vrrrrooooooooom brrrrrrrrr BRRRRRR wheeee BRRR brrrr
	 * VRRRRROOOOOOM ZOOOOOOM ZOOOOM WAHOOOOOOOOO WAHAHAHHA
	 * (Drives given a desired translation and rotation.)
	 * 
	 * @param translation   Desired translation in meters per second.
	 * @param rotation      Desired rotation in radians per second.
	 * @param fieldRelative If not field relative, the robot will move relative to
	 *                      its own rotation.
	 */
	public void drive(Translation2d translation, double rotation, boolean fieldRelative) {
		swerveDrive.drive(translation, rotation, fieldRelative, false);
	}

	/** Fetch the DriveCommands instance */
	public DriveCommands getCommands() {
		return commands;
	}

	/** Fetch the SwerveDrive instance */
	public SwerveDrive getSwerveDrive() {
		return swerveDrive;
	}

	public Vision getVision() {
		return vision;
	}

	/** Resets the Odemetry to the current position */
	public void resetOdometry(Pose2d pose2d) {
		swerveDrive.resetOdometry(new Pose2d(pose2d.getTranslation(), new Rotation2d(0)));
	}
}
