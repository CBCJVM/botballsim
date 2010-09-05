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
 * A configuration of sensors on board the iRobot Create.
 *  This monitors cliff amounts (basic reflectance) and bumps.
 */
public class CreateSensorConfig extends RobotConfig {
	/**
	 * Creates a new Create sensor configuration.
	 */
	public CreateSensorConfig() {
		// 6 sensors: 4 cliff reflectances and 2 bumpers
		super("create", new Sensor[6]);
		initSensors();
	}
	// Constructs and activates the 6 Create sensors.
	private void initSensors() {
		Sensor[] sensors = getSensors();
		// left and right bumps
		sensors[0] = new ButtonSensor(CollisionModels.fromFile("create-lbump"));
		sensors[1] = new ButtonSensor(CollisionModels.fromFile("create-rbump"));
		// cliff reflectance
		// TODO find exact places and set them
		sensors[2] = new ColorSensor(null);
		sensors[3] = new ColorSensor(null);
		sensors[4] = new ColorSensor(null);
		sensors[5] = new ColorSensor(null);
	}
}