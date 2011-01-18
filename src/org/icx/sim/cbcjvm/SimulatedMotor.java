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

import cbccore.low.Motor;
import org.icx.sim.BotballProgram;

/**
 * Contains stubs for botballsim.
 * Documentation stolen from the KISS-C documentation
 *
 * @author  Benjamin Woodruff, Braden McDorman
 * @see     cbccore.low.Motor
 */

public class SimulatedMotor extends Motor {
	
	protected BotballProgram bp;
	
	public SimulatedMotor(BotballProgram bp) { this.bp = bp; }
	
	
	public void motor(int motor, int percent) { bp.motor(motor, percent); }
	
	public int clear_motor_position_counter(int motor) {
		return bp.clear_motor_position_counter(motor);
	}
	
	public int move_at_velocity(int motor, int velocity) {
		return bp.move_at_velocity(motor, velocity);
	}
	
	public int mav(int motor, int velocity) {
		return bp.mav(motor, velocity);
	}
	
	public int move_to_position(int motor, int speed, int goal_pos) {
		return bp.move_to_position(motor, speed, goal_pos);
	}
	
	public int mtp(int motor, int speed, int goal_pos) {
		return bp.mtp(motor, speed, goal_pos);
	}
	
	public int move_relative_position(int motor, int speed, int delta_pos) {
		return bp.move_relative_position(motor, speed, delta_pos);
	}
	
	public int mrp(int motor, int speed, int delta_pos) {
		return bp.mtp(motor, speed, delta_pos);
	}
	
	public void set_pid_gains(int motor, int p, int i, int d, int pd, int id,
	                          int dd) {
		bp.set_pid_gains(motor, p, i, d, pd, id, dd);
	}
	
	public int freeze(int motor) {
		return bp.freeze(motor);
	}
	
	public int get_motor_done(int motor) {
		return bp.get_motor_done(motor);
	}
	
	public int get_motor_position_counter(int motor) {
		return bp.get_motor_position_counter(motor);
	}
	
	public void block_motor_done(int motor) {
		bp.block_motor_done(motor);
	}
	
	public void bmd(int motor) {
		bp.bmd(motor);
	}
	
	public int setpwm(int motor, int pwm) {
		return bp.setpwm(motor, pwm);
	}
	
	public int getpwm(int motor) {
		return bp.getpwm(motor);
	}
	
	public void fd(int motor) {
		bp.fd(motor);
	}
	
	public void bk(int motor) {
		bp.bk(motor);
	}
	
	public void off(int motor) {
		bp.off(motor);
	}
	
	public void ao() {
		bp.ao();
	}
}
