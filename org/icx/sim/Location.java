/*
 * This file is part of JBSim.
 * 
 * JBSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBSim.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.icx.sim;

/**
 * A class representing a location and vector (position and direction).
 */
public class Location implements java.io.Serializable {
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
		setX(x); setY(y); setVelocity(radius); setTheta(theta);
	}
	/**
	 * Creates a location from another location.
	 * 
	 * @param other the other location
	 */
	public Location(Location other) {
		this(other.x, other.y, other.r, other.t);
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
	/**
	 * Moves the location forward by the velocity in the direction.
	 * 
	 * @param dt the time difference in milliseconds
	 */
	public void increment(long dt) {
		x += r * Math.cos(t) * dt / 1000.;
		y += r * Math.sin(t) * dt / 1000.;
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
	public String toString() {
		return getClass().getCanonicalName() + "[x=" + x + ",y=" + y + ",mag=" + r +
			",dir=" + t + "]";
	}
}