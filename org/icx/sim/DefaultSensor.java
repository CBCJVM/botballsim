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
 * A class representing the default sensor (mostly for random mode).
 * 
 *  Do <b>not</b> extend this class; extend Sensor instead.
 */
public final class DefaultSensor extends Sensor {
	public static final String NAME = "Empty Port";

	// Gets the sensor's name.
	public static String getSensorName() {
		return NAME;
	}

	/**
	 * Creates a rather useless empty port sensor.
	 */
	public DefaultSensor() {
		this(null);
	}

	/**
	 * Creates a new default (featureless) sensor.
	 * 
	 * @param location the location on the robot, which may be <code>null</code>
	 */
	public DefaultSensor(Location location) {
		super(location);
	}

	/**
	 * Gets the sensor's name.
	 * 
	 * @return the sensor name
	 */
	public String getName() {
		return NAME;
	}

	// Returns value between 1020 and 1023 (typical for fixed-point CBC sensor)
	protected int realValue(Environment env) {
		return random(1020, 1023);
	}
}