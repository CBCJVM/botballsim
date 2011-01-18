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

package org.icx.sim.cbcjvm;

import cbccore.low.Servo;
import org.icx.sim.BotballProgram;

/**
 * Contains stubs for botballsim.
 * Documentation stolen from the KISS-C documentation
 *
 * @author  Benjamin Woodruff, Braden McDorman
 * @see     cbccore.low.Servo
 */

public class SimulatedServo extends Servo {
	
	protected BotballProgram bp;
	
	public SimulatedServo(BotballProgram bp) { this.bp = bp; }
	
	
	public void enable_servos() { bp.enable_servos(); }
	
	public void disable_servos() { bp.disable_servos(); }
	
	public int set_servo_position(int servo, int pos) {
		return set_servo_position(servo, pos);
	}
	
	public int get_servo_position(int servo) {
		return get_servo_position(servo);
	}
}
