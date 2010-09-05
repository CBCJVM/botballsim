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
 * Constants for robot-specific dimensions and attributes.
 * 
 * Unless otherwise specified:
 * - distances are in millimeters
 * - linear velocities are in mm/sec
 * - angles are in degrees
 * - angular velocities are in deg/sec
 */
public interface RobotConstants {
	// Radius of Create drive wheels from the center. Convenience only.
	public static final float CREATE_RADIUS = RobotsFile.getParameterFloat("create.radius");
	// How many mm there are per pixel = 5.15625.
	public static final float PIXELS_TO_MM = 330.f / 64.f;
	// How many pixels there are per mm = 0.19394.
	public static final float MM_TO_PIXELS = 64.f / 330.f;
}