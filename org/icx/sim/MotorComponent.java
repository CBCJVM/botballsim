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
import javax.swing.*;

/**
 * A class representing a motor or servo.
 * 
 * @author Stephen Carlson
 */
public class MotorComponent extends JComponent {
	private static final long serialVersionUID = 0L;

	// Icons for power, position, and stop.
	private static ImageIcon powerImg = Simulator.getIcon("power");
	private static ImageIcon posImg = Simulator.getIcon("pos");
	private static ImageIcon stopImg = Simulator.getIcon("dest");
	// Current power.
	//  Integer.MIN_VALUE = "Off", Integer.MAX_VALUE = "On", -100 to 100 = value, else: "Hold"
	private int power;
	// Current position.
	private long pos;
	// Destination position.
	//  Long.MAX_VALUE or Long.MIN_VALUE = "None", else = value
	private long dest;
	// The shaft angle in degrees.
	private int angle;
	// The label of the motor/servo.
	private String label;
	// The arrow which shows where the shaft is pointing.
	private GeneralPath arrow;

	/**
	 * Creates a new motor or servo with the given label.
	 * 
	 * @param label the item label on the motor body
	 */
	public MotorComponent(String label) {
		setFont(new Font("Courier New", Font.PLAIN, 14));
		setPreferredSize(new Dimension(80, 90));
		setForeground(Color.BLACK);
		setBorder(BorderFactory.createEtchedBorder());
		// initialize arrow graphic
		arrow = new GeneralPath(GeneralPath.WIND_NON_ZERO);
		arrow.moveTo(-4, 7);
		arrow.lineTo(4, 7);
		arrow.lineTo(0, 12);
		arrow.closePath();
		power = 0; angle = 0; pos = 0L; dest = Long.MAX_VALUE;
		this.label = label;
	}

	/**
	 * Sets the shaft angle of the motor/servo.
	 * 
	 * @param angle the angle in degres from 0 (which is pointing DOWN).
	 */
	public void setShaftAngle(int angle) {
		this.angle = angle;
		repaint();
	}

	/**
	 * Sets the power of the motor/servo.
	 * 
	 * @param power the power level:
	 * - from -100 to 100 to display it
	 * - absolute value > 100 to display "Hold"
	 */
	public void setPower(int power) {
		this.power = power;
		repaint();
	}

	/**
	 * Checks to see if the power is enabled.
	 * 
	 * @return true if power is enabled to this item and false otherwise
	 */
	public boolean isEnabled() {
		return power > Integer.MIN_VALUE && power != 0;
	}

	/**
	 * Changes the power level to "Off".
	 */
	public void servoDisable() {
		setPower(Integer.MIN_VALUE);
	}

	/**
	 * Changes the power level to "On".
	 */
	public void servoEnable() {
		setPower(Integer.MAX_VALUE);
	}

	/**
	 * Sets the current position of the motor/servo.
	 * 
	 * @param pos the real position in ticks (motor) or servo counts
	 */
	public void setPos(long pos) {
		this.pos = pos;
		repaint();
	}

	/**
	 * Sets the desired position of the motor/servo.
	 * 
	 * @param dest the destination in ticks/servo counts,
	 *  or either end bound for "None"
	 */
	public void setDest(long dest) {
		this.dest = dest;
		repaint();
	}
	public void update(Graphics g) {
		paint(g);
	}
	public void paint(Graphics g) {
		g.setColor(getBackground());
		// motor body
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.fillRect(5, 20, 70, 30);
		g.setFont(getFont());
		g.drawImage(powerImg.getImage(), 2, 2, null);
		// special cases for power level
		if (power == Integer.MIN_VALUE)
			g.drawString("Off", 20, 14);
		else if (power == Integer.MAX_VALUE)
			g.drawString("On", 20, 14);
		else if (dest == pos && Math.abs(power) > 100)
			g.drawString("Hold", 20, 14);
		else
			g.drawString(Integer.toString(power), 20, 14);
		// position and destination
		g.drawImage(posImg.getImage(), 2, 50, null);
		g.drawString(Long.toString(pos), 20, 64);
		g.drawImage(stopImg.getImage(), 2, 71, null);
		if (dest > Integer.MAX_VALUE || dest < Integer.MIN_VALUE)
			g.drawString("None", 20, 84);
		else
			g.drawString(Long.toString(dest), 20, 84);
		// motor label
		g.setColor(Color.WHITE);
		g.drawString(label, 55, 40);
		g.setColor(Color.GRAY);
		// shaft and pointer
		g.fillOval(7, 22, 26, 26);
		Graphics2D g2 = (Graphics2D)g.create();
		g2.translate(20, 35);
		g2.setColor(Color.RED);
		g2.rotate(Math.toRadians(angle));
		g2.fill(arrow);
		g2.dispose();
		// paint any borders
		super.paintBorder(g);
	}
}