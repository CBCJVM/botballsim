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

import cbccore.low.Sensor;
import org.icx.sim.BotballProgram;

/**
 * Contains stubs for botballsim.
 * Documentation stolen from the KISS-C documentation
 *
 * @author  Benjamin Woodruff, Braden McDorman
 * @see     cbccore.low.Sensor
 */

public class SimulatedSensor extends Sensor {
	
	protected BotballProgram bp;
	
	public SimulatedSensor(BotballProgram bp) { this.bp = bp; }
	
	
	public int digital(int port) { return bp.digital(port); }
	
	public int set_digital_output_value(int port, int value) {}
	
	public void set_analog_floats(int mask) {
		bp.set_analog_floats(mask);
	}
	
	public void set_each_analog_state(int a0, int a1, int a2, int a3, int a4,
	                                  int a5, int a6, int a7) {
		bp.set_each_analog_state(a0, a1, a2, a3, a4, a5, a6, a7);
	}
	
	public int analog10(int port) { return bp.analog10(port); }
	
	public int analog(int port) { return bp.analog(port); }
	
	public int accel_x() { return bp.accel_x(); }
	
	public int accel_y() { return bp.accel_y(); }
	
	public int accel_z() { return bp.accel_z(); }
	               
	public int sonar(int port) { return bp.sonar(port); }
	
	public int sonar_inches(int port) { return bp.sonar_inches(port); }
}
