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
 * A class representing an analog light sensor (infrared intensity) on the robot.
 */
public class LightSensor extends Sensor {
	public static final String NAME = "Starting Light Sensor";

	// Gets the sensor's name.
	public static String getSensorName() {
		return NAME;
	}

	/**
	 * Creates a starting light sensor of the given value type.
	 * 
	 * @param location the light sensor's location on the robot, or null if not physical
	 */
	public LightSensor(Location location) {
		super(location);
	}

	public String getName() {
		return NAME;
	}

	// I have not seen a non-construction use of a light sensor for anything other than
	//  the starting light on any robot. If someone uses it in another manner and really
	//  needs it to be simulated, add it here.
	protected int realValue(Environment env) {
		// any suggestions for realism are welcome.
		if (env.getStartingLight())
			return random(5, 100);
		else
			return random(920, 1015);
	}
}