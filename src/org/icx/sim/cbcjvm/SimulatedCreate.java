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

import cbccore.low.Create;
import org.icx.sim.BotballProgram;

/**
 * Contains stubs for botballsim.
 * Documentation stolen from the KISS-C documentation
 *
 * @author  Benjamin Woodruff, Braden McDorman
 * @see     cbccore.low.Create
 */

public class SimulatedCreate extends Create {
	
	protected BotballProgram bp;
	
	public SimulatedCreate(BotballProgram bp) { this.bp = bp; }
	
	/**
	 * First step for connecting CBC to Create. This function puts the Create in
	 * the create_safe mode.
	 * 
	 * @return        0 if sucessful and a negative number if not
	 * @see           #create_disconnect
	 */
	public int create_connect() { return bp.create_connect(); }
	
	
	
	/**
	 * Returns Create to proper state. Call this at the end of your program.
	 * 
	 * @see    #create_connect
	 */
	public void create_disconnect() { bp.create_disconnect(); }
	
	
	
	/**
	 * Puts Create into passive mode (no motors)
	 * 
	 * @see    #create_passive
	 * @see    #create_safe
	 * @see    #create_full
	 * @see    #create_mode
	 */
	public void create_start() { bp.create_start(); }
	
	
	
	/**
	 * Puts Create into passive mode (no motors)
	 * 
	 * @see    #create_start
	 * @see    #create_safe
	 * @see    #create_full
	 * @see    #create_mode
	 */
	public void create_passive() { bp.create_passive(); }
	
	
	
	/**
	 * Puts Create into safe mode. Create will execute all commands, but will
	 * disconnect and stop if drop or cliff sensors fire. This is recommended
	 * for practice, but not at a tournament.
	 * 
	 * @see    #create_passive
	 * @see    #create_full
	 * @see    #create_mode
	 */
	public void create_safe() { bp.create_safe(); }
	
	
	
	/**
	 * Puts Create into full mode. Create will move however you tell it -- even
	 * if that is a bad thing. In particular, the Create will not stop and
	 * disconnect, even if it is picked up or the cliff sensors fire. This is
	 * recommended for tournaments, but not for practice, due to its dangerous
	 * nature.
	 * 
	 * @see    #create_passive
	 * @see    #create_safe
	 * @see    #create_mode
	 */
	public void create_full() { bp.create_full(); }
	
	
	
	/**
	 * Simulates a Roomba doing a spot clean
	 * 
	 * @see    #create_cover
	 * @see    #create_demo
	 * @see    #create_cover_dock
	 */
	public void create_spot() { bp.create_spot(); }
	
	
	
	/**
	 * Simulates a Roomba covering a room
	 * 
	 * @see    #create_spot
	 * @see    #create_demo
	 * @see    #create_cover_dock
	 */
	public void create_cover() { bp.create_cover(); }
	
	
	
	/**
	 * Runs built in demos (see Create IO documentation)
	 * 
	 * @param  d  See Create IO documentation. I would normally look this up,
	 *                but it seems so pointless...
	 * @see       #create_spot
	 * @see       #create_cover
	 * @see       #create_cover_dock
	 */
	public void create_demo(int d) { bp.create_demo(); }
	
	
	
	/**
	 * Create roams around until it sees an IR dock and then attempts to dock
	 * 
	 * @see    #create_spot
	 * @see    #create_cover
	 * @see    #create_demo
	 */
	public void create_cover_dock() { bp.create_cover_dock(); }
	
	
	
	/**
	 * the Create's mode
	 * 
	 * @return   0 off; 1 passive; 2 safe; 3 full
	 * @see      #create_passive
	 * @see      #create_safe
	 * @see      #create_full
	 */
	public int create_mode() { return bp.create_mode(); }
	
	
	
	public int create_sensor_update() { return bp.create_sensor_update(); }
	public int create_wall() { return bp.create_wall(); }
	public int create_buttons() { return bp.create_buttons(); }
	public int create_bumpdrop() { return bp.create_bumpdrop(); }
	public int create_cliffs() { return bp.create_cliffs(); }
	public int create_angle() { return bp.create_angle(); }
	public int create_distance() { return bp.create_distance(); }
	public int create_velocity() { return bp.create_velocity(); }
	public int create_read_IR() { return bp.create_read_IR(); }
	public int create_overcurrents() { return bp.create_overcurrents(); }
	public int create_battery_charge() { return bp.create_battery_charge(); }
	public int create_cargo_bay_inputs() { return bp.create_cargo_bay_inputs();}
	
	
	
	/**
	 * Stops the drive wheels
	 */
	public void create_stop() { bp.create_stop(); }
	
	
	
	/**
	 * Drives in an arc.
	 * 
	 * @param  speed   range is 20-500mm/s
	 * @param  radius  radius in mm/s.<p>A radius of 32767 will drive the robot
	 *                     straight.
	 *                     <p>A radius of 1 will spin the robot CCW
	 *                     <p>A radius of -1 will spin the robot CW
	 *                     <p>Negative radii will be right turns, positive radii
	 *                     left turns
	 * @see            #create_drive_straight
	 */
	public void create_drive(int speed, int radius) {
		bp.create_drive(speed, radius);
	}
	
	
	
	/**
	 * Drives straight at speed in mm/s
	 * 
	 * @param  speed  20-500mm/s
	 */
	public void create_drive_straight(int speed) {
		bp.create_drive_straight(speed);
	}
	
	
	
	/**
	 * Spins CW with edge speed of speed in mm/s
	 * 
	 * @param  speed  20-500mm/s. Speed of edge (wheels) of bot.
	 * @see           #create_spin_CCW
	 */
	public void create_spin_CW(int speed) {
		bp.create_spin_CW(speed);
	}
	
	
	
	/**
	 * Spins CCW with edge speed of speed in mm/s
	 * 
	 * @param  speed  20-500mm/s. Speed of edge (wheels) of bot.
	 * @see           #create_spin_CW
	 */
	public void create_spin_CCW(int speed) {
		bp.create_spin_CCW(speed);
	}
	
	
	
	/**
	 * Specifies individual left and right speeds in mm/s
	 * 
	 * @param  r_speed  20-500mm/s. Speed of right wheel.
	 * @param  l_speed  20-500mm/s. Speed of left wheel.
	 * @see            #create_drive
	 */
	public void create_drive_direct(int r_speed, int l_speed) {
		bp.create_drive_direct(r_speed, l_speed);
	}
	
	
	
	/**
	 * This function blocks and does a pretty accurate spin. Note that the
	 * function will not return until the spin is complete
	 * CAUTION: requesting the robot to spin more than about 3600 degrees may
	 *                                                           never terminate
	 * 
	 * @param  speed  20-500mm/s. Speed of edge (wheels) of bot.
	 * @param  angle  Angle in degrees to turn before returning. <p>
	 *                   <b>CAUTION: requesting thce robot to spin more than
	 *                                about 3600 degrees may never terminate</b>
	 * @return        -1 if error
	 * @see           #create_spin_CW
	 * @see           #create_spin_CCW
	 */
	public int create_spin_block(int speed, int angle) {
		bp.create_spin_block(speed, angle);
	}
	
	
	
	// public native int _create_get_raw_encoders(long* lenc, long* renc);
	
	
	/**
	 * Turn on/off the advance LED
	 * 
	 * @param  on   1 to turn on light and 0 to turn it off
	 * @see         #create_play_led
	 * @see         #create_power_led
	 */
	public void create_advance_led(int on) {
		bp.create_advance_led(on);
	}
	
	
	
	/**
	 * Turn on/off the play LED
	 * 
	 * @param  on   1 to turn on light and 0 to turn it off
	 * @see         #create_advance_led
	 * @see         #create_power_led
	 */
	public void create_play_led(int on) {
		bp.create_play_led(on);
	}
	
	
	
	/**
	 * Control the color and the brightness of the power LED
	 * 
	 * @param  color       0 is red and 255 green
	 * @param  brightness  0 is off and 255 is full brightness
	 * @see                #create_advance_led
	 * @see                #create_play_led
	 */
	public void create_power_led(int color, int brightness) {
		bp.create_power_led(color, brightness);
	}
	
	
	
	/**
	 * This function sets the three digital out put pins 20,7,19 where 20 is the
	 * high bit and 19 is the low. You probably don't care about this function.
	 * 
	 * @param  bits  Should have a value 0 to 7.
	 */
	public void create_digital_output(int bits) {
		bp.create_digital_output(bits);
	}
	
	
	
	/**
	 * Sets the PWM signal for the three low side drivers (128 = 100%). You
	 * probably don't care about this function.
	 * 
	 * @param  pwm0   pin 22
	 * @param  pwm1   pin 23
	 * @param  pwm2   pin 24
	 */
	public void create_pwm_low_side_drivers(int pwm2, int pwm1, int pwm0) {}
	
	
	
	/**
	 * Turns on and off the signal for the three low side drivers (128 = 100%).
	 * A 0 or 1 should be given for each of the drivers to turn them off or on.
	 * You probably don't care about this function.
	 * 
	 * @param  pwm0   pin 22
	 * @param  pwm1   pin 23
	 * @param  pwm2   pin 24
	 */
	public void create_low_side_drivers(int pwm2, int pwm1, int pwm0) {}
	
	
	
	/**
	 * This loads a song into the robot's memory. Song can be numbered 0 to 15.
	 * The first element in each row of the array should be the number of notes
	 * (1-16) the subsequent pairs of bytes should be tone and duration see the
	 * roomba SCI manual for note codes. User's program should load the song
	 * data into the array before calling this routine. Sets gc_song_array, an
	 * inaccessable variable. <b>DO NOT USE THIS FUNCTION UNTIL THE ISSUE IS
	 * RESOLVED</b>
	 * 
	 * @param  num   Song can be numbered 0 to 15
	 * @see          #create_play_song
	 */
	public void create_load_song(int num) { bp.create_load_song(num); }
	
	
	
	
	/**
	 * See the roomba SCI manual for note codes. Uses gc_song_array, an
	 * inaccessable variable. <b>DO NOT USE THIS FUNCTION UNTIL THE ISSUE IS
	 * RESOLVED</b>
	 * 
	 * @param  num   Song can be numbered 0 to 15
	 * @see          #create_load_song
	 */
	public void create_play_song(int num) { bp.create_play_song(num); }
	
	
	
	public int create_read_block(byte[] buffer, int count) { return 0; }
	
	
	
	/**
	 * See Create IO Documentation. You probably don't care about this function.
	 * 
	 * @param  write_byte  the byte to write
	 * @see                #create_clear_serial_buffer
	 */
	public void create_write_byte(char write_byte) {}
	
	
	
	
	public void create_clear_serial_buffer() {}
	
	
	public int get_g_create_connected() { return bp.g_create_connected; }
	public int get_g_create_USB() { return bp.g_create_USB; }
	
	public int get_gc_lbump() { return bp.gc_lbump; }
	public int get_gc_rbump() { return bp.gc_rbump; }
	public int get_gc_ldrop() { return bp.gc_ldrop; }
	public int get_gc_rdrop() { return bp.gc_rdrop; }
	public int get_gc_fdrop() { return bp.gc_fdrop; }
	
	public int get_gc_rcliff() { return bp.gc_rcliff; }
	public int get_gc_rfcliff() { return bp.gc_rfcliff; }
	public int get_gc_lcliff() { return bp.gc_lcliff; }
	public int get_gc_lfcliff() { return bp.gc_lfcliff; }
	
	public int get_gc_rcliff_amt() { return bp.gc_rcliff_amt; }
	public int get_gc_rfcliff_amt() { return bp.gc_rfcliff_amt; }
	public int get_gc_lcliff_amt() { return bp.gc_lcliff_amt; }
	public int get_gc_lfcliff_amt() { return bp.gc_lfcliff_amt; }
	
	public int get_gc_distance() { return bp.gc_distance; }
	public int get_gc_angle() { return bp.gc_angle; }
	public int get_gc_total_angle() { return bp.gc_total_angle; }
	public int get_gc_advance_button() { return bp.gc_advance_button; }
	public int get_gc_play_button() { return bp.gc_play_button; }
	
	public int get_gc_wall() { return bp.gc_wall; }
	public int get_gc_wall_amt() { return bp.gc_wall_amt; }
	public int get_gc_wall_hb() { return bp.gc_wall_hb; }
	public int get_gc_IR() { return bp.gc_IR; }
	
	public int get_gc_vel() { return bp.gc_vel; }
	public int get_gc_radius() { return bp.gc_radius; }
	public int get_gc_rvel() { return bp.gc_rvel; }
	public int get_gc_lvel() { return bp.gc_lvel; }
	
	public int get_gc_overcurrents() { return bp.gc_overcurrents; }
	public int get_gc_batt_charge() { return bp.gc_batt_charge; }
	public int get_gc_batt_capacity() { return bp.gc_batt_capacity; }
	
	public int get_gc_charge_state() { return bp.gc_charge_state; }
	public int get_gc_batt_voltage() { return bp.gc_batt_voltage; }
	public int get_gc_current_flow() { return bp.gc_current_flow; }
	public int get_gc_batt_temp() { return bp.gc_batt_temp; }
	
	public int get_gc_digital_in() { return bp.gc_digital_in; }
	public int get_gc_analog_in() { return bp.gc_analog_in; }
	public int get_gc_charge_source() { return bp.gc_charge_source; }
	
	public int[][] get_gc_song_array() { return bp.gc_song_array; }
	
	public int get_gc_mode() { return bp.gc_mode; }
}
