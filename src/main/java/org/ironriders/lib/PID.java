package org.ironriders.lib;

public class PID {
    public PID(double p) {
        this.p = p;
    }

    public PID(double p, double i) {
        this.p = p;
        this.i = i;
    }

    public PID(double p, double i, double d) {
        this.p = p;
        this.i = i;
        this.d = d;
    }

    public double p;
    public double i;
    public double d;
}
