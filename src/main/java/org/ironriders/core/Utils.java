package org.ironriders.core;

public class Utils {

    /**
     * Applies a control curve (currently just an exponential function)
     * Only works with input values from 0 to 1 because 1^x = 1.
     * 
     * @param input    The value to put into the curve, clamped between 0 and 1.
     * @param exponent The exponent value. (e.g. 3 for cubic curve)
     * @param deadband The deadband value. (e.g. 0.1 for 10% deadband)
     * @return The end result of the curve.
     */
    public static double controlCurve(double input, double exponent, double deadband) {
        
        // Clamp the input between 0 and 1
        input = Math.min(1, Math.max(0, input));

        return Math.pow(input, exponent);
    }
}
