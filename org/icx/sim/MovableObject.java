package org.icx.sim;

/**
 * An abstract moveable object. Currently only parents robots,
 *  but will eventually parent all scoring objects.
 * 
 * @author Stephen Carlson
 */
public abstract class MovableObject extends SimObject {
	public MovableObject(String image) {
		super(image);
	}

	public void setLocation(Location loc) {
		super.setLocation(loc);
	}
}