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

import java.util.*;

/**
 * A class holding the entire simulation environment.
 *  This was separated from the simulator to allow it to be passed around easily.
 */
public class Environment {
	// All robots (currently only one).
	private LinkedList<SimRobot> robots;
	// All non-robot objects in the simulation.
	private List<SimObject> items;
	// Only one starting light.
	private boolean light;

	/**
	 * Creates a new, empty environment.
	 */
	public Environment() {
		robots = new LinkedList<SimRobot>();
		items = new ArrayList<SimObject>(100);
		light = false;
	}

	/**
	 * Adds an object to the simulation (wall, static, etc.)
	 * 
	 * @param obj the object to add
	 */
	public void add(StaticObject obj) {
		items.add(obj);
	}

	/**
	 * Deletes an object from the simulation (wall, static, etc.)
	 *  No delete robot with this method!!!
	 * 
	 * @param obj the object to remove
	 */
	public void remove(StaticObject obj) {
		items.remove(obj);
	}

	/**
	 * Adds a robot to the simulation. Currently, only the first robot will ever
	 *  be used (single support). Fixing this wil not be overly difficult, but
	 *  will require multiple windows/tabs for all of the inputs.
	 * 
	 * @param r the robot to add
	 */
	public void addRobot(SimRobot r) {
		r.reset();
		robots.add(r);
	}

	/**
	 * Removes a robot from the simulation.
	 * 
	 * @param r the robot to remove
	 */
	public void removeRobot(SimRobot r) {
		r.reset();
		robots.remove(r);
	}

	/**
	 * Gets a list of the robots in the environment.
	 * 
	 * @return a list of the robots available
	 */
	public List<SimRobot> getRobots() {
		return robots;
	}

	/**
	 * Gets the first robot, which is the only one currently run.
	 * 
	 * @return the robot to simulate
	 */
	public SimRobot getFirstRobot() {
		if (robots.size() == 0) return null;
		return robots.getFirst();
	}

	/**
	 * Gets a list of all objects in the environment.
	 * 
	 * @return a list of the objects available
	 */
	public List<SimObject> getObjects() {
		return items;
	}

	/**
	 * Returns whether the starting light is on.
	 * 
	 * @return whether the light is on
	 */
	public boolean getStartingLight() {
		return light;
	}

	/**
	 * Sets whether the starting light is on.
	 * 
	 * @param light to turn the light on or off
	 */
	public void setLight(boolean light) {
		this.light = light;
	}
}