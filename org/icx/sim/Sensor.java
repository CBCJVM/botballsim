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
import java.lang.reflect.*;
import javax.swing.*;
import java.util.*;

/**
 * A class that stores information about a sensor, and provides a
 *  mechanism for retrieving its value.
 */
public abstract class Sensor {
	// Allows reconstruction of this configuration, without
	//  need for setRobotType()
	public static final PersistenceDelegate SENSOR_DEL =
		new DefaultPersistenceDelegate(new String[] { "location", "type" });
	// Someone who knows Java better than me should modify this
	//  to do it like Java should do it; have sensors "register"
	//  their availability.
	public static final Class<Sensor>[] SENSOR_TYPES = new Class[] {
		ColorSensor.class, DistanceSensor.class, LightSensor.class, ButtonSensor.class,
		DefaultSensor.class
	};
	// Names of sensors initialized by populateSensorNames()
	private static String[] SENSOR_NAMES;

	// To be overridden by subclasses.
	public static String getSensorName() {
		return "Unknown Sensor Type";
	}

	// Populates the sensor names array.
	private static void populateSensorNames() {
		if (SENSOR_NAMES == null) {
			SENSOR_NAMES = new String[SENSOR_TYPES.length];
			for (int i = 0; i < SENSOR_TYPES.length; i++) {
				Class<Sensor> clazz = SENSOR_TYPES[i];
				try {
					// get sensor properties from class
					Method method = clazz.getDeclaredMethod("getSensorName", (Class<?>[])null);
					String ans = (String)method.invoke(null, (Object[])null);
					if (ans != null && ans.length() > 0)
						SENSOR_NAMES[i] = ans;
					else
						SENSOR_NAMES[i] = getSensorName();
				} catch (Exception e) {
					// no method declared
					SENSOR_NAMES[i] = getSensorName();
				}
			}
		}
	}

	/**
	 * Fetches a new sensor, prompting the user about its attributes.
	 * 
	 * @return a new sensor configured for use, or null if canceled
	 */
	public static Sensor getSensor(Simulator parent) {
		populateSensorNames();
		// get name
		String ans = (String)JOptionPane.showInputDialog(parent,
			"Select a sensor type:", "Sensor Type", JOptionPane.QUESTION_MESSAGE,
			null, SENSOR_NAMES, SENSOR_NAMES[0]);
		if (ans == null) return null;
		// reverse look up
		int index = -1;
		for (int i = 0; i < SENSOR_NAMES.length; i++)
			if (SENSOR_NAMES[i].equalsIgnoreCase(ans)) index = i;
		if (index < 0 || index >= SENSOR_TYPES.length) return null;
		// for now: user inputs location in (x, y)
		String locString = JOptionPane.showInputDialog(parent,
			"Enter sensor location:", "0.0, 0.0");
		StringTokenizer str = new StringTokenizer(locString, ",");
		if (str.countTokens() < 2) return null;
		try {
			// get location
			Location loc = new Location(Double.parseDouble(str.nextToken().trim()),
				Double.parseDouble(str.nextToken().trim()));
			// try to instantiate a sensor
			return (Sensor)SENSOR_TYPES[index].getConstructors()[0].
				newInstance(loc);
		} catch (Exception e) {
			return null;
		}
	}

	// The robot on which the sensor is installed.
	protected SimRobot robot;
	// Sensor location relative to robot center.
	protected Location loc;

	// Creates a sensor at the given location with the given value type.
	protected Sensor(Location location) {
		loc = location;
	}

	/**
	 * Gets the sensor's location on the robot.
	 * 
	 * @return the sensor's position and direction relative to robot
	 *  position and direction
	 */
	public Location getLocation() {
		return loc;
	}

	/**
	 * Returns location transformed to world coordinates.
	 *  This can be slow, so cache it if you can. Location is fresh, so it
	 *  can be modified if need be.
	 * 
	 * @return the location transformed to real world
	 */
	public Location getRealLocation() {
		Location oldLoc = getLocation();
		Location loc = new Location(oldLoc);
		if (robot == null) return loc;
		Location rLoc = robot.getLocation();
		// simple and sure
		if (oldLoc.getX() == 0 && oldLoc.getY() == 0 && oldLoc.getTheta() == 0)
			return rLoc;
		// set heading, keep vector mag (probably useless)
		loc.setTheta(oldLoc.getTheta() + rLoc.getTheta());
		// set location, rotate point around axis
		double sinT = Math.sin(rLoc.getTheta()), cosT = Math.cos(rLoc.getTheta());
		loc.setX(rLoc.getX() - sinT * oldLoc.getY() + cosT * oldLoc.getX());
		loc.setY(rLoc.getY() + cosT * oldLoc.getY() + sinT * oldLoc.getX());
		return loc;
	}

	/**
	 * Gets the parent robot.
	 * 
	 * @return the robot on which the sensor is installed
	 */
	public SimRobot getParentRobot() {
		return robot;
	}

	/**
	 * Returns the sensor's display name.
	 *  Should return the same result as getSensorName().
	 * 
	 * @return a name describing the sensor
	 */
	public abstract String getName();

	/**
	 * Sets the parent robot.
	 * 
	 * @param robot the new parent robot
	 */
	public void setParentRobot(SimRobot robot) {
		this.robot = robot;
	}

	/**
	 * Gets the sensor's current value. Set values should be handled elsewhere.
	 * 
	 * @param env the Environment in which the sensor is found
	 * @return the sensor value
	 */
	public int getValue(Environment env) {
		return realValue(env);
	}

	/**
	 * Checks the sensor's current value as a digital.
	 * 
	 * @param env the Environment in which the sensor is found
	 * @return the value as true/false
	 */
	public boolean digitalValue(Environment env) {
		return getValue(env) < 512;
	}

	/**
	 * Constructs a value from a digital state. The digitalValue of
	 *  a sensor in real mode which has realValue return the result
	 *  from this method is guaranteed to equal <code>on</code>.
	 * 
	 * @param on whether the digitalValue is to be on or off
	 * @return the value suitable for a return from realValue()
	 */
	public static int createDigitalValue(boolean on) {
		return on ? 1 : 1022;
	}

	/**
	 * Gets the real value of this sensor.
	 * 
	 * @param env the environment in which the sensor is located
	 * @return the sensor value, scaled from 0-1023
	 */
	protected abstract int realValue(Environment env);

	/**
	 * Returns a random sensor value in the given bounds.
	 * 
	 * @param low the lower bound
	 * @param high the upper bound
	 * @return a value between those bounds inclusive
	 */
	public int random(int low, int high) {
		// Find a way to replicate these outputs?
		//  But if you're using random in the first place, you want it to be random, right?
		return (int)Math.floor(Math.random() * (high - low + 1) + low);
	}
}