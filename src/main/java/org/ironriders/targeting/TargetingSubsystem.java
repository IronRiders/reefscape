package org.ironriders.targeting;

import java.util.Optional;

import org.ironriders.lib.field.FieldElement;
import org.ironriders.lib.field.FieldPose;
import org.ironriders.lib.field.FieldElement.ElementType;
import org.ironriders.lib.field.FieldPose.Level;
import org.ironriders.lib.field.FieldPose.Side;
import org.ironriders.drive.DriveCommands;
import org.ironriders.drive.DriveSubsystem;
import org.ironriders.lib.GameState;
import org.ironriders.lib.IronSubsystem;

/**
 * Handles targeting of field elements for autonomous movement.
 */
public class TargetingSubsystem extends IronSubsystem {

	private Optional<FieldElement> targetElement = Optional.empty();
	private Optional<FieldPose> poseAtTargetElement = Optional.empty();
	private Optional<ElementType> targetElementType = Optional.empty();

	private boolean targetNearestElement = true;
	private int targetSlot = FieldPose.STATION_SLOT_COUNT / 2;
	
	private Side targetPole = Side.Left;
	private Level targetLevel = Level.L1;
	private TargetingCommands commands;

	public TargetingSubsystem() {
		commands = new TargetingCommands(this);
		GameState.setTargetRobotPose(this::getTargetPose);
	}

	public TargetingCommands getCommands() {
		return commands;
	}

	public void setTargetSlot(int slot) {
		targetSlot = slot;
	}

	public int getTargetSlot() {
		return targetSlot;
	}

	public void setTargetPole(Side side) {
		targetPole = side;
	}

	public Side getTargetPole() {
		return targetPole;
	}

	public void setTargetLevel(Level level) {
		targetLevel = level;
	}

	public Level getActivelevel() {
		return targetLevel;
	}

	public void targetNearest() {
		targetNearestElement = true;
		targetElementType = Optional.empty();
	}

	public void targetNearest(ElementType type) {
		targetNearestElement = true;
		targetElementType = Optional.of(type);
	}

	public void setTargetElement(FieldElement element) {
		targetElement = Optional.of(element);
		setTargetNearest(false);
	}

	public void setTargetElement(Optional<FieldElement> element) {
		if (element.isEmpty()) {
			setTargetNearest(true);
		} else {
			setTargetNearest(false);
		}
	}

	public void setTargetNearest(boolean value) {
		targetNearestElement = value;
	}

	public Optional<FieldPose> getTargetPose() {
		if (targetElement.isEmpty()) {
			return Optional.empty();
		}

		var element = targetElement.get();
		switch (element.type) {
			case STATION:
				return Optional.of(new FieldPose.Station(element, targetSlot));

			case REEF:
				return Optional.of(new FieldPose.Reef(element, targetPole, targetLevel));

			default:
				return Optional.of(new FieldPose(element));
		}
	}

	@Override
	public void periodic() {
		if (targetNearestElement) {
			findNearestElement();
		}

		var pose = getTargetPose();
		if (pose.isEmpty()) {
			return;
		}
		if (poseAtTargetElement.isPresent() && pose.get().equals(poseAtTargetElement.get())) {
			return;
		}

		GameState.getField().getObject("Target").setPose(pose.get().toPose2d());
		poseAtTargetElement = pose;
	}

	private void findNearestElement() {
		var robotPose = GameState.getRobotPose();
		if (robotPose.isEmpty()) {
			return;
		}

		if (targetElementType.isPresent()) {
			targetElement = FieldElement.nearestTo(robotPose.get(), targetElementType.get());
		} else {
			targetElement = FieldElement.nearestTo(robotPose.get());
		}
	}
}
