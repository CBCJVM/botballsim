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
	 * @param center the center of the object
	 * @param width the width in mm
	 * @param height the height in mm
	 * @return the bounding box
	 */
	public Area createBoundingBox(Location center, float width, float height) {
		float x = (float)center.getX(), y = (float)center.getY();
		Rectangle2D bounds = new Rectangle2D.Float(x - width / 2, y - width / 2, width, height);
		// inefficient! suggestions?
		Area shape = new Area(bounds);
		shape.transform(AffineTransform.getRotateInstance(center.getTheta(), x, y));
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
		obj = new DisplayObject(image);
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
		obj = new DisplayObject(image);
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
}