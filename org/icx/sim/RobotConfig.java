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

import java.beans.*;

/**
 * A class that stores configuration information about a robot -
 *  sensor, start position, type, and more.
 */
public class RobotConfig implements java.io.Serializable {
	// Allows reconstruction of this configuration, without
	//  need for setRobotType()
	public static final PersistenceDelegate del =
		new DefaultPersistenceDelegate(new String[] { "type" });

	// The robot type (immutable)
	private String type;
	// The start location.
	private Location start;
	// The analog sensors.
	private AnalogSlider[] analog;
	private LockingButton[] digital;

	// Creates a robot configuration with the specified type.
	public RobotConfig(String robotType) {
		type = robotType;
	}
}