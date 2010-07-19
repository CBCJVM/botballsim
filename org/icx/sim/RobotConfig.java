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

import java.io.*;
import java.beans.*;

/**
 * A class that stores configuration information about a robot -
 *  sensor, start position, type, and more.
 */
public class RobotConfig {
	// Allows reconstruction of this configuration, without
	//  need for setRobotType()
	public static final PersistenceDelegate CONFIG_DEL =
		new DefaultPersistenceDelegate(new String[] { "type", "sensors" });
	// Silences all non-fatal errors!
	private static final ExceptionListener silence = new SimExceptionListener();

	/**
	 * Reads in a robot configuration from the given stream.
	 * 
	 * @param is the input stream to read
	 * @return the decoded robot config
	 */
	public static RobotConfig fromStream(InputStream is) {
		XMLDecoder decode = new XMLDecoder(is);
		decode.setExceptionListener(silence);
		try {
			return (RobotConfig)decode.readObject();
		} catch (Throwable t) {
			return null;
		}
	}

	// The robot type (immutable)
	private String type;
	// The start location.
	private Location start;
	// The robot sensors from 0-15, where 0-7 are analog and 8-15 are digital.
	//  Generally, a null slot means value is set and should be read from screen.
	private Sensor[] sensors;

	/**
	 * Creates a robot configuration with the specified type.
	 * 
	 * @param robotType the robot type (name from the robots.txt file) to use
	 */
	public RobotConfig(String robotType) {
		this(robotType, new Sensor[15]);
	}

	/**
	 * Creates a robot configuration with the specified type and sensor setup.
	 * 
	 * @param robotType the robot type (name from the robots.txt file) to use
	 * @param sensors the sensor configuration to use
	 */
	public RobotConfig(String robotType, Sensor[] sensors) {
		type = robotType;
		this.sensors = sensors;
		start = new Location();
		fillUnusedPorts();
	}

	/**
	 * Fills in unused ports with default sensors.
	 * 
	 * Until a consistent distinction between "null" and DefaultSensor() is
	 *  established, avoid this method.
	 */
	public void fillUnusedPorts() {
		for (int i = 0; i < sensors.length; i++)
			if (sensors[i] == null)
				sensors[i] = new DefaultSensor();
	}

	/**
	 * Gets the starting position.
	 * 
	 * @return the location where the robot should start
	 */
	public Location getStart() {
		return start;
	}

	/**
	 * Changes the starting position.
	 * 
	 * @param start the robot's new starting position
	 */
	public void setStart(Location start) {
		this.start = start;
	}

	/**
	 * Gets the robot type.
	 * 
	 * @return the robot type to index in robots.txt
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the sensors installed on the robot.
	 * 
	 * @return the sensor specs
	 */
	public Sensor[] getSensors() {
		return sensors;
	}

	/**
	 * Gets the sensor installed on the given port.
	 * 
	 * @param port the port to read
	 * @return the sensor, or null if none is installed (use set value)
	 *  Returns of null must always be checked for, but expect an instance
	 *  of DefaultSensor for any useful applications.
	 */
	public Sensor getSensor(int port) {
		if (port < 0 || port >= sensors.length) return null;
		return sensors[port];
	}

	/**
	 * @Deprecated Use getSensor(port) instead and check for nulls.
	 * 
	 * Returns a non-null representative of the sensor on the given port.
	 * 
	 * @param port the port to read
	 * @return the sensor, or a new instance of DefaultSensor if none is installed
	 */
	@Deprecated
	public Sensor getSensorSafe(int port) {
		Sensor sense = getSensor(port);
		if (sense == null) return new DefaultSensor();
		else return sense;
	}

	/**
	 * Writes the XML-encoded robot configuration to the given stream.
	 * 
	 * @param os the output stream to write
	 * @return whether the operation completed successfully
	 */
	public boolean toStream(OutputStream os) {
		XMLEncoder encode = new XMLEncoder(os);
		encode.setExceptionListener(silence);
		// set delegates for this class and all sensors
		encode.setPersistenceDelegate(RobotConfig.class, CONFIG_DEL);
		encode.setPersistenceDelegate(Sensor.class, Sensor.SENSOR_DEL);
		for (Class<Sensor> type : Sensor.SENSOR_TYPES)
			encode.setPersistenceDelegate(type, Sensor.SENSOR_DEL);
		// set up objects
		try {
			encode.writeObject(this);
			encode.close();
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	// Writes the XML-encoded version out to a stream
	public String toString() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream(32768);
		toStream(stream);
		return stream.toString();
	}

	// Dummy exception listener to ignore when an error occurs
	private static class SimExceptionListener implements ExceptionListener {
		public void exceptionThrown(Exception e) {
			//e.printStackTrace();
		}
	}
}