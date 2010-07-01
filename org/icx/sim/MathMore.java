package org.icx.sim;

import java.util.Arrays;

/**
 * A class that contains optimized-time implementations of some
 *  functions in java.lang.Math.
 * 
 * @author Stephen Carlson
 */
public final class MathMore {
	/**
	 * Trig table for sine, cosine, secant, cosecant, arcsine, arccosine,
	 *  arcsecant, and arccosecant.
	 */
	private static double[] sin;
	/**
	 * Trig table for tangent, cotangent, arctangent, and arccotangent.
	 */
	private static double[] tan;
	/**
	 * Whether the fast trig functions are ready.
	 */
	private static boolean ready = false;
	/**
	 * The smallest number that can really make a difference for floats.
	 */
	public static final float FPE_F = 1E-7f;
	/**
	 * The smallest number that can really make a difference for doubles.
	 */
	public static final double FPE_D = 1E-11;

	/**
	 * Initializes the fast trig functions to work their magic.
	 */
	public static synchronized final void initFastTrig() {
		ready = false;
		sin = new double[91];
		tan = new double[91];
		double deg;
		for (int i = 0; i < 91; i++) {
			deg = Math.toRadians(i);
			sin[i] = Math.sin(deg);
			tan[i] = Math.tan(deg);
		}
		ready = true;
	}

	/**
	 * Finds the sine of an angle in DEGREES.
	 * 
	 * @param x the angle in degrees
	 * @return the sine of the angle
	 */
	public static double sin(double x) {
		if (!ready) initFastTrig();
		int x2 = (int)Math.round(x) % 360;
		if (x2 < 0) x2 += 360;
		if (x2 > 270) return -sin[360 - x2];
		if (x2 > 180) return -sin[x2 - 180];
		if (x2 > 90) return sin[180 - x2];
		return sin[x2];
	}
	/**
	 * Returns the angle whose tangent is y/x. Always returns the
	 *  correct answer in DEGREES, within 2 degrees of the actual
	 *  answer. Great for GUIs. NOT TO BE USED IN A SIMULATION!!!
	 * 
	 * @param y the y coordinate
	 * @param x the x coordinate
	 * @return the angle in degrees, between -180 and 180 inclusive
	 */
	public static int atan2(double y, double x) {
		if (!ready) initFastTrig();
		if (doubleEquals(x, 0.)) {
			if (y < 0.) return -180;
			return 180;
		}
		if (y < 0. && x < 0.)
			return atan2(-y, -x) - 180;
		if (y < 0.)
			return -atan2(-y, x);
		if (x < 0.)
			return 180 - atan2(y, -x);
		int res = Arrays.binarySearch(tan, y / x);
		if (res >= 0) return res;
		return -res - 1;
	}
	/**
	 * Finds the secant of an angle in DEGREES.
	 * 
	 * @param x the angle in degrees
	 * @return the secant of the angle
	 */
	public static double sec(double x) {
		return 1. / cos(x);
	}
	/**
	 * Finds the cosine of an angle in DEGREES.
	 * 
	 * @param x the angle in degrees
	 * @return the cosine of the angle
	 */
	public static double cos(double x) {
		return sin(90. - x);
	}
	/**
	 * Finds the cosecant of an angle in DEGREES.
	 * 
	 * @param x the angle in degrees
	 * @return the cosecant of the angle
	 */
	public static double csc(double x) {
		return 1. / sin(x);
	}
	/**
	 * Finds the tangent of an angle in DEGREES.
	 * 
	 * @param x the angle in degrees
	 * @return the tangent of the angle
	 */
	public static double tan(double x) {
		if (!ready) initFastTrig();
		int x2 = (int)Math.round(x) % 180;
		if (x2 < 0) x2 += 180;
		if (x2 > 90) return -tan[180 - x2];
		return tan[x2];
	}
	/**
	 * Finds the cotangent of an angle in DEGREES.
	 * 
	 * @param x the angle in degrees
	 * @return the cotangent of the angle
	 */
	public static double cot(double x) {
		return 1. / tan(x);
	}
	/**
	 * Finds the distance between two points.
	 * 
	 * @param x1 the x coordinate of point 1
	 * @param x2 the x coordinate of point 1
	 * @param y1 the y coordinate of point 2
	 * @param y2 the y coordinate of point 2
	 * @return the distance
	 */
	public static double distance(double x1, double x2, double y1, double y2) {
		return Math.hypot(y2 - y1, x2 - x1);
	}
	/**
	 * Finds the distance between two points in 3d.
	 * 
	 * @param x1 the x coordinate of point 1
	 * @param x2 the x coordinate of point 1
	 * @param y1 the y coordinate of point 2
	 * @param y2 the y coordinate of point 2
	 * @param z1 the z coordinate of point 3
	 * @param z2 the z coordinate of point 3
	 * @return the distance
	 */
	public static double distance(double x1, double x2, double y1, double y2,
			double z1, double z2) {
		double d1 = distance(x1, y1, x2, y2);
		return Math.hypot(d1, z2 - z1);
	}
	/**
	 * Compares two doubles for equality. Takes FPE into account.
	 * 
	 * @param one the first number
	 * @param two the second number
	 * @return equality
	 */
	public static boolean doubleEquals(double one, double two) {
		return Math.abs(one - two) < FPE_D;
	}
	/**
	 * Compares two floats for equality. Takes FPE into account.
	 * 
	 * @param one the first number
	 * @param two the second number
	 * @return equality
	 */
	public static boolean floatEquals(float one, float two) {
		return Math.abs(one - two) < FPE_F;
	}
	/**
	 * Squares a double and returns the answer.
	 * 
	 * @param x the number to square
	 * @return x ^ 2
	 */
	public static double square(double x) {
		return x * x;
	}
}