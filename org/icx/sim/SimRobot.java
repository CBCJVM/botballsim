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

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * A class that represents the user robot.
 * 
 * @author Stephen Carlson
 */
public class SimRobot extends MovableObject {
	// Robot controller type
	private String type;
	// The parent simulator
	private Simulator parent;
	// Left and right wheel speeds in mm/sec
	private int lvel;
	private int rvel;

	// Controller types available
	public static final String RCX = "rcx";
	public static final String HB = "hb";
	public static final String XBC = "xbc";
	public static final String CBC = "cbc2";
	public static final String CBC_V1 = "cbc";
	// The order in which controllers naturally progress
	//  used heavily to determine which functions are available on which types
	public static final String[] SORT_ORDER = new String[] {
		RCX, HB, XBC, CBC_V1, CBC
	};

	// CACHED: Information from RobotsFile for this bot.
	private float radius;
	private float factor;
	private Area model;
	private String drive;

	// The robot's sensor and initial setup.
	private RobotConfig setup;
	// Any auxiliary setup required. For Creates mostly.
	private RobotConfig auxSetup;

	/**
	 * Creates a new simulated robot.
	 * 
	 * @param parent the simulator which owns this robot
	 * @param robotType the robot type to use
	 */
	public SimRobot(Simulator parent, String robotType) {
		super(RobotsFile.getParameter(robotType + ".icon"));
		this.parent = parent;
		auxSetup = null;
		// set type and drive from config file
		type = RobotsFile.getParameter(robotType + ".type");
		drive = RobotsFile.getParameter(robotType + ".map");
		factor = 1.f;
		if (drive.equals("none"));
		else if (drive.equals("gc")) {
			// Create specialization
			radius = RobotConstants.CREATE_RADIUS;
			auxSetup = new CreateSensorConfig();
		} else {
			radius = RobotsFile.getParameterFloat(robotType + ".radius");
			factor = RobotsFile.getParameterFloat(robotType + ".factor");
		}
		// read collision model
		model = CollisionModels.fromFile(RobotsFile.getParameter(robotType + ".model"));
		setup = new RobotConfig(robotType);
		reset();
	}

	/**
	 * Used to set the robot's code behavior.
	 * 
	 * @return controller type
	 */
	public String getController() {
		return type;
	}

	/**
	 * Checks to see if the controller is newer than the given model.
	 *  Models are ordered by Botball appearance, with the HB between the RCX and XBC.
	 * 
	 * @param compare the controller to compare
	 * @return whether the controller is newer than or equal to the model specified
	 */
	public boolean controllerAtLeast(String compare) {
		if (compare == null) return true;
		int index1 = pos(SORT_ORDER, compare), index2 = pos(SORT_ORDER, getController());
		if (index1 < 0) return true;
		if (index2 < 0) return false;
		return index1 <= index2;
	}

	/**
	 * Resets this robot to its starting position.
	 */
	public void reset() {
		setSpeeds(0, 0);
		// copy to avoid messing with setup
		setLocation(new Location(setup.getStart()));
	}

	/**
	 * Gets this robot's configuration.
	 * 
	 * @return the robot configuration
	 */
	public RobotConfig getSetup() {
		return setup;
	}

	/**
	 * Gets the auxiliary setup. If drive was "gc", returns the Create setup;
	 *  other setups might be returned in the future.
	 * Note that starting location is still read from main setup.
	 * 
	 * @return the secondary robot setup
	 */
	public RobotConfig getAuxSetup() {
		return auxSetup;
	}

	/**
	 * Checks to see if the controller is exactly the specified one.
	 * 
	 * @param compare the controller type to check
	 * @return whether this robot is that controller
	 */
	public boolean controllerEquals(String compare) {
		return getController().equals(compare);
	}

	/**
	 * Indexes the given array to find the given string.
	 * 
	 * @param array the array to index
	 * @param toFind the string to find
	 * @return the index, or -1 if not found
	 */
	private static int pos(String[] array, String toFind) {
		for (int i = 0; i < array.length; i++)
			if (toFind.equalsIgnoreCase(array[i])) return i;
		return -1;
	}

	/**
	 * Returns the drive mapping, which dictate how motor speeds
	 *  appear from the robots' outputs.
	 * 
	 * @return the drive type.
	 * Possible values:
	 *  gc - use Create (drive=gc means that Create library is enabled)
	 *  motor#,# - use motor ports
	 *  none - no mapping
	 */
	public String getDrive() {
		return drive;
	}

	/*
	 * If multi robot control is to be implemented, these delegate methods
	 *  need to be modified to point to different simulator windows or panes
	 * 
	 * Detailed documentation is available in Simulator.java
	 */

	public void ao() {
		parent.ao();
	}

	public int buttonMask() {
		return parent.buttonMask();
	}

	public void clearLCD() {
		parent.clearLCD();
	}

	public void disableServos() {
		parent.disableServos();
	}

	public void enableServos() {
		parent.enableServos();
	}

	public void refreshLCD() {
		parent.refreshLCD();
	}

	public void print(String text, boolean flush) {
		parent.print(text, flush);
	}

	public void print(String text) {
		parent.print(text);
	}

	public void printf(String text, Object... args) {
		synchronized (parent) {
			// Fixed the caret bug with this statement.
			parent.getLCDWriter().format(text, args);
			parent.refreshLCD();
		}
	}

	public MotorComponent getMotor(int port) {
		return parent.getMotor(port);
	}

	public MotorComponent getServo(int port) {
		return parent.getServo(port);
	}

	public boolean getBlackButton() {
		return parent.getBlackButton();
	}

	public Area getCollision() {
		return model;
	}

	/**
	 * Sets the speeds of each wheel in the unit defined by the controller.
	 *  This could be mm/sec (particularly Create) or anything else provided
	 *  that robots.txt has a non-unity factor of conversion.
	 * 
	 * @param l the left wheel speed
	 * @param r the right wheel speed
	 */
	public void setSpeeds(int l, int r) {
		lvel = l; rvel = r;
	}

	/**
	 * Modifies the robot's location from wheel speeds in mm/sec.
	 * 
	 * @param dt the time difference is milliseconds across which interval is computed
	 * @param collisions the colliding objects
	 */
	public void move(long dt, List<SimObject> collisions) {
		// where + is CCW and - is CW
		float torque = (lvel * factor - rvel * factor) / 2.f;
		// convert "torque" to angular velocity, rad / sec
		float omega = torque / radius;
		Location dest = getLocation(), dir;
		// Not really forces and torques, but suitable enough names.
		float force = (lvel * factor + rvel * factor) / 2.f;
		for (SimObject obj : collisions) {
			// use the slide vector to kill velocities
			dir = obj.hitDirection(this);
			if (dir == null) {
				// stop now
				force = 0.f; break;
			} else {
				System.out.println("Collision, || vector direction=" +
					Math.toDegrees(dir.getTheta()) + ", personal direction=" +
					Math.toDegrees(dest.getTheta()) + ", loc=" + dest);
				// adjust force to "component in direction of given vector"
				force = force * (float)Math.cos(dest.getTheta() - dir.getTheta());
			}
		}
		// rotate robot
		dest.setTheta(dest.getTheta() + omega * dt / 1000.f);
		// install force
		dest.setVelocity(force);
		dest.increment(dt);
	}

	// Paints this robot's sensors.
	protected void paintComponent(Graphics2D g) {
		// main first
		g.setColor(Color.BLUE);
		renderSensors(g, setup);
		// then aux
		if (auxSetup != null) {
			g.setColor(Color.RED);
			renderSensors(g, auxSetup);
		}
	}

	// Renders the sensors in the setup on the robot.
	protected void renderSensors(Graphics2D g, RobotConfig setup) {
		Location loc;
		int rx, ry;
		for (Sensor sense : setup.getSensors())
			if (sense != null && (loc = sense.getLocation()) != null) {
				// find integer coordinates
				rx = (int)Math.round(loc.getX());
				ry = (int)Math.round(loc.getY());
				// TODO directional indication
				g.fillRect(rx - 2, ry - 2, 4, 4);
			}
	}

	/**
	 * Collides with the given object.
	 * 
	 * @param env the world in which to collide
	 * @param dt the time difference in milliseconds across which interval is computed
	 * @return whether moving would collide
	 */
	public List<SimObject> collide(long dt) {
		List<SimObject> ret = new LinkedList<SimObject>();
		Location loc = new Location(getLocation());
		moveLocation(loc, dt);
		Area shape = getTransformedCollision(), test;
		for (SimObject obj : parent.getEnvironment().getObjects()) {
			if (obj == this) continue;
			// do a good intersection
			test = new Area(obj.getTransformedCollision());
			test.intersect(shape);
			if (!test.isEmpty()) ret.add(obj);
		}
		return ret;
	}

	/**
	 * Modifies a location from wheel speeds in mm/sec.
	 * 
	 * @param dest the location to modify
	 * @param dt the time difference in milliseconds across which interval is computed
	 */
	public void moveLocation(Location dest, long dt) {
		// Not really forces and torques, but suitable enough names.
		float force = (lvel * factor + rvel * factor) / 2.f;
		// where + is CCW and - is CW
		float torque = (rvel * factor - lvel * factor) / 2.f;
		// install force
		dest.setVelocity(force);
		// convert "torque" to angular velocity, rad / sec
		float omega = torque / radius;
		// rotate robot
		dest.setTheta(dest.getTheta() + omega * dt / 1000.f);
		dest.increment(dt);
	}

	/**
	 * Gets the analog value of this sensor, after factoring in the type.
	 * 
	 * @param port the port to read
	 * @return the real value of the sensor
	 */
	public int analog(int port) {
		Sensor input;
		AnalogSlider pInput = parent.getAnalog(port);
		if (port > 7 || (input = setup.getSensor(port)) == null || pInput.getValueType() == 0)
			// special (accel_x, accel_y, accel_z) or not set up
			return pInput.getValue();
		// ugly
		input.setParentRobot(this);
		return input.getValue(parent.getEnvironment());
	}

	/**
	 * Gets the analog value from auxiliary configuration of this sensor,
	 *  after factoring in the type.
	 * 
	 * @param port the port to read
	 * @return the real value of the sensor
	 */
	public int extra_analog(int port) {
		Sensor input;
		if (auxSetup == null || (input = auxSetup.getSensor(port)) == null)
			// special or not set up, but can't delegate to parent
			return 1023;
		// ugly
		input.setParentRobot(this);
		return input.getValue(parent.getEnvironment());
	}

	/**
	 * Gets the digital value of this sensor, after factoring in the type.
	 * 
	 * @param port the port to read
	 * @return the real value of the sensor
	 */
	public boolean digital(int port) {
		Sensor input;
		LockingButton pInput = parent.getDigital(port);
		if ((input = setup.getSensor(port)) == null || pInput.getValueType() == 0)
			// not set up
			return pInput.isSelected();
		// ugly
		input.setParentRobot(this);
		return input.digitalValue(parent.getEnvironment());
	}

	/**
	 * Gets the digital value from auxiliary configuration of this sensor,
	 *  after factoring in the type.
	 * 
	 * @param port the port to read
	 * @return the real value of the sensor
	 */
	public boolean extra_digital(int port) {
		Sensor input;
		if (auxSetup == null || (input = auxSetup.getSensor(port)) == null)
			// special or not set up, but can't delegate to parent
			return false;
		// ugly
		input.setParentRobot(this);
		return input.digitalValue(parent.getEnvironment());
	}
}