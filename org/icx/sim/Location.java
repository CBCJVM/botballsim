package org.icx.sim;

/**
 * A class representing a location and vector (position and direction).
 */
public class Location implements Comparable<Location>, java.io.Serializable {
	public static final long serialVersionUID = -789234167023416723L;

	private double x;    // The x position.
	private double y;    // The y position.
	private double r;    // The velocity.
	private double t;    // The velocity direction.

	/**
	 * Creates a zero velocity object at the origin.
	 */
	public Location() {
		x = y = 0;
		r = t = 0;
	}
	/**
	 * Creates a zero velocity object at the specified place.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Location(double x, double y) {
		this();
		setX(x); setY(y);
	}
	/**
	 * Creates an object with the specified parameters.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param radius the velocity in mm/sec
	 * @param theta the angle
	 */
	public Location(double x, double y, double radius, double theta) {
		this();
		setX(x); setY(y); setVelocity(radius); setTheta(theta);
	}
	/**
	 * Gets the velocity of this object.
	 * 
	 * @return the velocity
	 */
	public double getVelocity() {
		return r;
	}
	/**
	 * Changes the velocity of this object.
	 * 
	 * @param radius the new velocity
	 */
	public void setVelocity(double radius) {
		r = radius;
	}
	/**
	 * Gets the direction of this object.
	 * 
	 * @return the direction in rads
	 */
	public double getTheta() {
		return t;
	}
	/**
	 * Changes the direction of this object
	 * 
	 * @param theta the new direction in rads
	 */
	public void setTheta(double theta) {
		t = theta;
	}
	/**
	 * Gets the x coordinate of this object.
	 * 
	 * @return the x coordinate
	 */
	public double getX() {
		return x;
	}
	/**
	 * Changes the x coordinate of this object.
	 * 
	 * @param x the new x coordinate
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * Gets the y coordinate of this object.
	 * 
	 * @return the y coordinate
	 */
	public double getY() {
		return y;
	}
	/**
	 * Changes the y coordinate of this object.
	 * 
	 * @param y the new y coordinate
	 */
	public void setY(double y) {
		this.y = y;
	}
	// Tests this object with another for equality.
	public boolean equals(Object o) {
		if (!(o instanceof Location)) return false;
		else return equals((Location)o);
	}
	// Tests this object with another for equality.
	public boolean equals(Location l) {
		return (l.x == x && l.y == y && l.r == r && l.t == t);
	}
	public int compareTo(Location l) {
		if (!MathMore.doubleEquals(l.x, x)) return (int)Math.signum(l.x - x);
		else if (!MathMore.doubleEquals(l.y, y)) return (int)Math.signum(l.y - y);
		else if (!MathMore.doubleEquals(l.r, r)) return (int)Math.signum(l.r - r);
		else if (!MathMore.doubleEquals(l.t, t)) return (int)Math.signum(l.t - t);
		else return 0;
	}
	public String toString() {
		return getClass().getCanonicalName() + "[x=" + x + ",y=" + y + ",vr=" + r +
			",vt=" + t + "]";
	}
}