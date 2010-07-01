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
	// Radius of Create drive wheels from the center.
	public static final float CREATE_RADIUS = 90.f;
	// How many pixels there are per mm.
	public static final float PIXELS_TO_MM = 1.f;
	// How many mm there are per pixel.
	public static final float MM_TO_PIXELS = 1.f / PIXELS_TO_MM;
}