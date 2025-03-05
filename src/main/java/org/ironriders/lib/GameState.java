package org.ironriders.lib;

import java.util.Optional;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

/**
 * Current robot state required by multiple subsystems.
 */
public class GameState {
    static private Field2d field = new Field2d();
    static private Supplier<Optional<Pose2d>> robotPose = () -> Optional.empty();
    static private Supplier<Optional<FieldPose>> targetRobotPose = () -> Optional.empty();

    private GameState() {}

    public static Field2d getField() {
        return field;
    }

    public static void setField(Field2d field) {
        GameState.field = field;
    }

    public static Optional<Pose2d> getRobotPose() {
        return robotPose.get();
    }

    public static void setRobotPose(Supplier<Optional<Pose2d>> robotPose) {
        GameState.robotPose = robotPose;
    }

    public static Optional<FieldPose> getTargetRobotPose() {
        return targetRobotPose.get();
    }

    public static void setTargetRobotPose(Supplier<Optional<FieldPose>> robotPose) {
        GameState.targetRobotPose = robotPose;
    }
}
