package org.ironriders.elevator;

import static org.ironriders.core.Constants.*;

public enum Level {
    L1 (L1Height),
    L2 (L2Height),
    L3 (L3Height),
    L4 (L4Height);

    public double height;

    private Level(double height){
        this.height = height;
    }
}
