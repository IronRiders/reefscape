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
}
