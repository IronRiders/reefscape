package org.ironriders.elevator;

import static org.ironriders.elevator.ElevatorConstants.*;

public enum Level {
    Down (downPos),
    L1 (L1Height),
    L2 (L2Height),
    L3 (L3Height),
    L4 (L4Height);

    public double positionInches;

    private Level(double positionInches){
        this.positionInches = positionInches;
    }
}
