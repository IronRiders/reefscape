package org.ironriders.core;


public class Utils {

    /**
     * Applies a control curve (currently just an exponential function)
     * Only works with input values from 0 to 1 because 1^x = 1.
     * 
     * @param input The value to put into the curve (0.0 - 1.0 ONLY)
     * @param deadband The exponent value.
     * @return The end result of the curve.
     */
    public static double controlCurve(double input, double exponent, double deadband) {
                return Math.pow(input, exponent);
    }



     /**
     * Normalizes a rotational input value to the range [0, 360) degrees.
     *
     * @param input The input rotational value.
     * @return The normalized rotational value within the range [0, 360) degrees.
     */
    public static double absoluteRotation(double input) {
        return (input % 360 + 360) % 360;
    }
    /**
     * Normalizes Added Voltage from Feed Forward to a number between (0.0, 1.0).
     *
     * @param input The additional voltage.
     * @return The normalized volatge value within the range (0.0, 1.0).
     */
    public static double percentOfMaxVoltage(double voltage, int maxVoltage){
        return (voltage/maxVoltage);
    }

    public static double clamp(double min,double max,double in){
        if(in>max){
            in=max;
        }
        if(in<min){
            in=min;
        }
        return in;
    }
    

}
