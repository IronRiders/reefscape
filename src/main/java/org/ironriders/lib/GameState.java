package org.ironriders.lib;

import java.util.Optional;
import java.util.function.Supplier;

import org.ironriders.Manipulators.Elevator.ElevatorConstants;
import org.ironriders.lib.field.FieldPose;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

/**
 * Current robot state required by multiple subsystems.
 */
public class GameState {

    static private Field2d field = new Field2d();
    static private Supplier<Optional<Pose2d>> robotPose = () -> Optional.empty();
    static private Supplier<Optional<FieldPose>> targetRobotPose = () -> Optional.empty();

    // these represent our current elevator targets for their respective game pieces.
    static private ElevatorConstants.Level coralTarget = ElevatorConstants.Level.L1;
    static private ElevatorConstants.Level algaeTarget = ElevatorConstants.Level.L2;

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

    public static ElevatorConstants.Level getCoralTarget() {
        return coralTarget;
    }

    public static void setCoralTarget(ElevatorConstants.Level coralTarget) {
        GameState.coralTarget = coralTarget;
    }

    public static ElevatorConstants.Level getAlgaeTarget() {
        return algaeTarget;
    }

    public static void setAlgaeTarget(ElevatorConstants.Level algaeTarget) {
        GameState.algaeTarget = algaeTarget;
    }
}
