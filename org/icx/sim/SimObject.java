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

import java.awt.geom.*;

/**
 * Any simulated object on the screen is represented by this class.
 * 
 * @author Stephen Carlson
 */
public abstract class SimObject {
	/**
	 * Creates a bounding box collision model for the object.
	 * 
	 * @param width the width in mm
	 * @param height the height in mm
	 * @return the bounding box
	 */
	public static Area createBoundingBox(float width, float height) {
		Rectangle2D bounds = new Rectangle2D.Float(width / -2.f,
			height / -2.f, width, height);
		// inefficient! suggestions?
		Area shape = new Area(bounds);
		return shape;
	}
	/**
	 * The object ID.
	 */
	protected int id;
	/**
	 * The component which displays this object on the screen.
	 */
	protected DisplayObject obj;

	/**
	 * Creates a simulated object with the given image.
	 * 
	 * @param image the object image
	 */
	public SimObject(String image) {
		obj = new DisplayObject(this, image);
	}

	/**
	 * Gets the object location and rotation.
	 * 
	 * @return the coordinates
	 */
	public Location getLocation() {
		return obj.getLocation();
	}

	/**
	 * Sets the color of the object.
	 * 
	 * @param color the color to tint the object
	 */
	public void setTint(java.awt.Color color) {
		obj.setColor(color);
	}

	/**
	 * Changes the location of the object.
	 * 
	 * @param loc the new location
	 */
	protected void setLocation(Location loc) {
		obj.setLocation(loc);
	}

	/**
	 * Changes the image for this object.
	 * 
	 * @param image the new image name
	 */
	// Note: reinstantiates DisplayObject, cache frequently used images
	public void setImage(String image) {
		obj = new DisplayObject(this, image);
	}

	/**
	 * Gets the object ID.
	 * 
	 * @return the ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Gets the drawable object for the screen.
	 * 
	 * @return the component responsible for drawing the object to the screen
	 */
	public DisplayObject getDrawable() {
		return obj;
	}

	/**
	 * Gets the collision model.
	 * 
	 * @return a shape for collision
	 */
	public abstract Area getCollision();

	/**
	 * For collisions, many objects have a directional vector, so that robots will
	 *  properly slide along it. The vector must have <b>unit length</b>.
	 *  Returning null will cause the object to halt colliding bots.
	 * 
	 * @param hitObject the simulation object that is colliding
	 * @return the vector parallel to sliding direction, or null if not applicable
	 */
	public Location hitDirection(SimObject hitObject) {
		return null;
	}

	/**
	 * Gets the transformed collision model.
	 * 
	 * @return the collision model ready for checking
	 */
	public Area getTransformedCollision() {
		Area test = new Area(getCollision());
		Location loc = getLocation();
		test.transform(AffineTransform.getRotateInstance(
			loc.getTheta()));
		test.transform(AffineTransform.getTranslateInstance(
			loc.getX(), loc.getY()));
		return test;
	}
}