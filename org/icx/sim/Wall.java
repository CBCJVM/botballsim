package org.icx.sim;

import java.awt.geom.Area;

/**
 * A class which represents a PVC wall.
 * 
 * @author Stephen Carlson
 */
public class Wall extends StaticObject {
	// Maps PVC indices to images.
	private static final String[] types = new String[] {
		"pvc", "pvc-t", "pvc-inter", "pvc-corner"
	};

	// Multiple kinds of PVC are available.
	public static final int TYPE_PVC = 0;
	public static final int TYPE_PVC_T = 1;
	public static final int TYPE_PVC_INTERSECTION = 2;
	public static final int TYPE_PVC_CORNER = 3;

	// Straight segments are N-S or E-W.
	public static final int DIR_LTR = 1;
	public static final int DIR_TTB = 0;

	// PVC joiners can point in 4 directions.
	public static final int POINT_EAST = 3;
	public static final int POINT_SOUTH = 2;
	public static final int POINT_WEST = 1;
	public static final int POINT_NORTH = 0;

	// PVC straight segment length
	private int length;
	// PVC joiner direction
	private int direction;

	/**
	 * Creates a new static wall of the given type.
	 * 
	 * @param type the wall type
	 * @param dir the direction (use TTB/LTR for TYPE_PVC and POINT_* for others)
	 * @param len the length (for TYPE_PVC) 
	 */
	public Wall(int type, int dir, int len) {
		super(types[type]);
		length = len;
		direction = dir;
		updateSize();
	}
	/**
	 * Gets the PVC direction.
	 * 
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}
	/**
	 * Changes the PVC direction.
	 * 
	 * @param direction the new direction
	 */
	public void setDirection(int direction) {
		this.direction = direction;
		updateSize();
	}
	/**
	 * Gets the PVC length.
	 * 
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	/**
	 * Changes the PVC length.
	 * 
	 * @param length the new length
	 */
	public void setLength(int length) {
		this.length = length;
		updateSize();
	}
	// Updates location information to match PVC dimensions
	private void updateSize() {
		obj.setHeight(length);
		obj.setWidth(-1);
		Location loc = getLocation();
		if (loc != null) loc.setTheta(Math.PI * direction / 2);
	}
	public void setLocation(Location loc) {
		super.setLocation(loc);
		updateSize();
	}
	public Area getCollision() {
		return createBoundingBox(getLocation(), obj.getSetWidth() * RobotConstants.PIXELS_TO_MM,
			obj.getSetHeight() * RobotConstants.PIXELS_TO_MM);
	}
}