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

import java.awt.geom.Area;

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

	// TODO controller types are not differentiated, and many are unimplemented
	//  should have a "BotballProgramXBC.java", "BotballProgramCBCV2.java"...
	//  create should be broken up into xbc create, cbc create, ...
	//  create is properly differentiated from others in modelling
	public static final String XBC = "xbc";
	public static final String CBC = "cbc2";
	public static final String CBC_V1 = "cbc";
	public static final String CREATE = "create";
	public static final String[] AVAILABLE = new String[] { CBC, CREATE };
	// if this is to be done later, BotballProgram.java has the correct
	//  mappings for which functions are on which platforms.

	// CACHED: Information from RobotsFile for this bot.
	private float radius;
	private float factor;

	/**
	 * Creates a new simulated robot.
	 * 
	 * @param parent the simulator which owns this robot
	 * @param robotType the robot type to use
	 */
	public SimRobot(Simulator parent, String robotType) {
		super(RobotsFile.getParameter(robotType + ".icon"));
		this.parent = parent;
		type = robotType;
		setSpeeds(0, 0);
		radius = RobotsFile.getParameterFloat(robotType + ".radius");
		factor = RobotsFile.getParameterFloat(robotType + ".factor");
	}

	/**
	 * Used to set graphic, collision, etc.
	 * 
	 * @return controller type
	 */
	public String getController() {
		return type;
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

	public void pause() {
		parent.pause();
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
		// TODO based on robot type
		//  fetch from RobotConstants?
		return null;
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
	 * Modifies a location from wheel speeds in mm/sec.
	 * 
	 * @param dt the time difference is milliseconds across which interval is computed
	 */
	public void move(long dt) {
		Location dest = getLocation();
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
}