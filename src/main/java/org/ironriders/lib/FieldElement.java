package org.ironriders.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Representation of an element on the field.
 * 
 * Includes pose, function, and various utility routines for retrieving.
 */
public class FieldElement {
    /**
     * The type of elements on the field. 
     */
    public enum ElementType {
        STATION,
        REEF,
        PROCESSOR,
        BARGE,
    }

    /**
     * Generic (alliance-independent) element identifiers.
     */
    public enum Position {
        LEFT_STATION(0, ElementType.STATION),
        RIGHT_STATION(1, ElementType.STATION),
        REEF_FRONT(2, ElementType.REEF),
        REEF_LEFT_FRONT(3, ElementType.REEF),
        REEF_LEFT_BACK(4, ElementType.REEF),
        REEF_BACK(5, ElementType.REEF),
        REEF_RIGHT_BACK(6, ElementType.REEF),
        REEF_RIGHT_FRONT(7, ElementType.REEF),
        PROCESSOR(8, ElementType.PROCESSOR),
        BARGE(9, ElementType.BARGE);

        final public int id;
        final public ElementType type;

        Position(int id, ElementType type) {
            this.id = id;
            this.type = type;
        }
    }

    final public Position position;
    final public ElementType type;
    final public Pose3d pose;
    final public String name;
    
    private static int[] BLUE_TAGS = {
        13,
        12,
        18,
        19,
        20,
        21,
        22,
        17,
        16,
        14
    };

    private static int[] RED_TAGS = {
        1,
        2,
        7,
        6,
        11,
        10,
        9,
        8,
        3,
        5,
    };

    private static List<FieldElement> BLUE_ELEMENTS = loadElements(DriverStation.Alliance.Blue, BLUE_TAGS);
    private static List<FieldElement> RED_ELEMENTS = loadElements(DriverStation.Alliance.Red, RED_TAGS);

    private FieldElement(Position element, Pose3d pose) {
        this.position = element;
        this.type = element.type;
        this.name = element.name();
        this.pose = pose;
    }

    /**
     * Retrieve all elements for an alliance.
     */
    public static Collection<FieldElement> of(Optional<DriverStation.Alliance> alliance) {
        if (alliance.isEmpty()) {
            return new ArrayList<FieldElement>();
        }

        if (alliance.get().equals(DriverStation.Alliance.Red)) {
            return RED_ELEMENTS;
        }

        return BLUE_ELEMENTS;
    }

    /**
     * Retrieve a specific alliance element.
     */
    public static Optional<FieldElement> of(Position element) {
        for (var allianceElement : of(DriverStation.getAlliance())) {
            if (allianceElement.position == element) {
                return Optional.of(allianceElement);
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieve the closest alliance element of a desired type.
     */
    public static Optional<FieldElement> nearestTo(Pose2d pose, ElementType type) {
        return findNearest(pose, Optional.of(type));
    }

    /**
     * Find a special alliance element.
     */
    public static Optional<FieldElement> nearestTo(Pose2d pose) {
        return findNearest(pose, Optional.empty());
    }
    
    private static List<FieldElement> loadElements(DriverStation.Alliance alliance, int[] tags) {
        return Stream.of(Position.values())
            .map(element -> {
                var pose = FieldUtils.FIELD_LAYOUT.getTagPose(tags[element.id]);
                if (pose.isEmpty()) {
                    Optional.empty();
                }
                return Optional.of(new FieldElement(element, pose.get()));
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
    
    private static Optional<FieldElement> findNearest(Pose2d pose, Optional<ElementType> type) {
        double distance = -1;
        Optional<FieldElement> found = Optional.empty();

        for (var element : of(DriverStation.getAlliance())) {
            if (type.isPresent() && element.type != type.get()) {
                continue;
            }

            double thisDistance = pose.getTranslation().getDistance(element.pose.toPose2d().getTranslation());
            if (found.isEmpty() || distance < thisDistance) {
                distance = thisDistance;
                found = Optional.of(element);
            }
        }

        return found;
    }
}
