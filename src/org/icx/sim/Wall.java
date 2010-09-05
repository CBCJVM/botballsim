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
	// The type of the game.
	private int type;
	// The collision box.
	private Area collide;

	/**
	 * Creates a new static wall of the given type.
	 * 
	 * @param type the wall type
	 * @param dir the direction (use TTB/LTR for TYPE_PVC and POINT_* for others)
	 * @param len the length (for TYPE_PVC) 
	 */
	public Wall(int type, int dir, int len) {
		super(types[type]);
		collide = null;
		length = len;
		direction = dir;
		this.type = type;
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
	protected void updateSize() {
		if (type == TYPE_PVC) {
			obj.setHeight(length);
			obj.setWidth(-1);
			collide = createBoundingBox(5 * RobotConstants.PIXELS_TO_MM,
				length * RobotConstants.PIXELS_TO_MM);
		} else {
			obj.setHeight(-1);
			obj.setWidth(-1);
			collide = createBoundingBox(5 * RobotConstants.PIXELS_TO_MM,
				5 * RobotConstants.PIXELS_TO_MM);
		}
		Location loc = getLocation();
		if (loc != null) loc.setTheta(Math.PI * direction / 2);
	}
	public void setLocation(Location loc) {
		super.setLocation(loc);
		updateSize();
	}
	public Area getCollision() {
		return collide;
	}
	public Location hitDirection(SimObject hitObject) {
		if (type != TYPE_PVC) return null;
		Location loc = new Location(getLocation());
		// slides along pipe
		loc.setTheta(Math.PI * (1 + direction) / 2);
		loc.setVelocity(1);
		return loc;
	}
}