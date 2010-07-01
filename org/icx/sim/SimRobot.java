package org.icx.sim;

import java.awt.geom.Area;

/**
 * A class that represents the user robot.
 * 
 * @author Stephen Carlson
 */
public class SimRobot extends MovableObject {
	private String type;
	private Simulator parent;

	// TODO controller types are not differentiated, and many are unimplemented
	//  should have a "BotballProgramXBC.java", "BotballProgramCBCV2.java"...
	//  create should be broken up into xbc create, cbc create, ...
	//  create is properly differentiated from others in modelling
	public static final String XBC = "xbc";
	public static final String CBC = "cbc2";
	public static final String CBC_V1 = "cbc1";
	public static final String CREATE = "create";
	public static final String[] AVAILABLE = new String[] { CBC, CREATE };
	// if this is to be done later, BotballProgram.java has the correct
	//  mappings for which functions are on which platforms.

	public SimRobot(Simulator parent, String robotType) {
		super(robotType);
		this.parent = parent;
		type = robotType;
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
}