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
 * A class representing an analog ET sensor (physical distance) on the robot.
 */
public class DistanceSensor extends Sensor {
	public static final String NAME = "ET Sensor";

	// Gets the sensor's name.
	public static String getSensorName() {
		return NAME;
	}

	/**
	 * Creates a distance sensor of the given value type.
	 * 
	 * @param location the ET sensor's location on the robot, or null if not physical
	 */
	public DistanceSensor(Location location) {
		super(location);
	}

	public String getName() {
		return NAME;
	}

	protected int realValue(Environment env) {
		return 0;
	}
}