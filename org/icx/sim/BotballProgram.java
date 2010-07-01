package org.icx.sim;

import java.lang.reflect.*;
import java.util.*;

/**
 * A class which provides Botball library functions.
 * 
 *  TODO fix up the inconsistent interface between this file and Simulator.java
 * 
 * @author Stephen Carlson
 */
public abstract class BotballProgram {
	// Constants (#define in standard library)
	public static final int A_BUTTON = 1;
	public static final int B_BUTTON = 2;
	public static final int CHOOSE_BUTTON = 1;
	public static final int CANCEL_BUTTON = 2;
	public static final int LEFT_BUTTON = 4;
	public static final int RIGHT_BUTTON = 8;
	public static final int UP_BUTTON = 16;
	public static final int DOWN_BUTTON = 32;
	// Internal Create mode constants
	private static final int _MODE_SAFE = 2;
	private static final int _MODE_FULL = 3;
	private static final int _MODE_PASSIVE = 1;
	// Variables defined by Create library for Create sensors and status
	protected int g_create_connected, g_create_USB;
	protected int gc_lbump, gc_rbump, gc_ldrop, gc_rdrop, gc_fdrop;
	protected int gc_rcliff, gc_rfcliff, gc_lcliff, gc_lfcliff;
	protected int gc_rcliff_amt, gc_rfcliff_amt, gc_lcliff_amt, gc_lfcliff_amt;
	protected int gc_distance, gc_angle, gc_total_angle, gc_advance_button, gc_play_button;
	protected int gc_wall, gc_wall_amt, gc_wall_hb, gc_IR;
	protected int gc_vel, gc_radius, gc_rvel, gc_lvel;
	protected int gc_overcurrents, gc_batt_charge, gc_batt_capacity;
	protected int gc_charge_state, gc_batt_voltage, gc_current_flow, gc_batt_temp;
	protected int gc_digital_in, gc_analog_in, gc_charge_source;
	protected int[][] gc_song_array;
	protected int gc_mode;
	// Internal variables not to be accessed by user code.
	private float _shutdown;    // Used by shut_down_in
	private int _gc_l, _gc_r;   // raw Create wheel velocities in mm/sec
	private int[] _gc_leds;     // Create LED status
	private long[] _dest;       // the PID destinations in ticks, absolute
	private int[] _speed;       // the PID control velocity, ticks/second
	private int[] _vel;         // the actual movement velocity, scale 0-100%
	private long[] _counts;     // the BEMF counters
	private int[] _pos;         // servo positions
	private int[] _loc;         // servo actual locations
	private boolean _servos_enabled = false;
	private List<UserThread> _threads; // all user threads
	private int _nextID;        // next available thread ID
	private Simulator _sim;     // parent simulator
	private SimRobot _bot;      // robot to control
	private long _start;        // timing variables
	private long _total;
	private UserThread pidTask; // moves servos and motors

	/**
	 * Initializes the most important variables
	 */
	public BotballProgram() {
		_threads = new LinkedList<UserThread>();
		_nextID = 0; _shutdown = 0.f;
		g_create_connected = g_create_USB = 0;
		_gc_l = _gc_r = gc_mode = 0;
		gc_song_array = new int[16][33];
		_gc_leds = new int[3];
		_dest = new long[4];
		_speed = new int[4];
		_vel = new int[4];
		_counts = new long[4];
		_pos = new int[4];
		_loc = new int[4];
		for (int i = 0; i < 4; i++)
			_pos[i] = _loc[i] = 1023;
		_sim = null;
		_start = _total = 0L;
		pidTask = null;
	}
	// CREATE LIBRARY
	/**
	 * Checks for a Create connection and prints warning message if not connected.
	 */
	private void _nc() {
		_s();
		if (g_create_connected == 0) _bot.print("No Create connection mode is 0\n");
	}
	/**
	 * Checks for a Create connection.
	 * 
	 * @return whether the Create was connected
	 */
	private boolean _ic() {
		return g_create_connected != 0;
	}
	// Create Library: connect to Create
	public int create_connect() {
		_s();
		if (!_ic()) {
			g_create_USB = 1;
			g_create_connected = 1;
			create_start();
			create_advance_led(1);
			create_safe();
			return 0;
		} else return -1;
	}
	// Create Library: disconnect from Create
	public void create_disconnect() {
		_nc();
		if (_ic()) {
			create_power_led(0, 255);
			create_stop();
			gc_mode = 0;
			g_create_connected = 0;
		}
	}
	// Create Library: returns active Create mode
	public int create_mode() {
		_s();
		if (!_ic()) return 0;
		return gc_mode;
	}
	// Create Library: puts Create in safe mode
	public void create_safe() {
		_nc();
		if (_ic()) {
			gc_mode = _MODE_SAFE;
			create_power_led(64, 255);
		}
	}
	// Create Library: puts Create in full mode
	public void create_full() {
		_nc();
		if (_ic()) {
			gc_mode = _MODE_FULL;
			create_power_led(227, 255);
		}
	}
	// Create Library: puts Create in passive mode
	public void create_passive() {
		_nc();
		if (_ic()) {
			gc_mode = _MODE_PASSIVE;
			create_power_led(0, 255);
		}
	}
	// Create Library: alias for compatibility
	public void create_start() {
		create_passive();
	}
	// Create Library: spot clean
	public void create_spot() {
		_nc();
		if (_ic()) _bot.print("Simulator doesn't currently support spot clean\n");
	}
	// Create Library: cover
	public void create_cover() {
		_nc();
		if (_ic()) _bot.print("Simulator doesn't currently support cover\n");
	}
	// Create Library: cover and dock
	public void create_cover_dock() {
		_nc();
		if (_ic()) _bot.print("Simulator doesn't currently support cover dock\n");
	}
	// Create Library: play demo
	public void create_demo(int n) {
		_nc();
		if (_ic()) _bot.print("Simulator doesn't currently support demos\n");
	}
	// Create Library: update all sensors
	public int create_sensor_update() {
		_nc();
		if (!_ic()) return -1;
		create_buttons();
		create_wall();
		create_bumpdrop();
		create_angle();
		create_distance();
		create_velocity();
		create_battery_charge();
		create_read_IR();
		create_cargo_bay_inputs();
		create_cliffs();
		return 0;
	}
	// Create Library: update bumps and wheel drops
	public int create_bumpdrop() {
		_nc();
		if (!_ic()) return -1;
		// FIXME
		gc_lbump = gc_rbump = gc_ldrop = gc_rdrop = gc_fdrop = 0;
		return 0;
	}
	// Create Library: update cliffs
	public int create_cliffs() {
		_nc();
		if (!_ic()) return -1;
		// FIXME
		gc_rcliff = gc_rfcliff = gc_lcliff = gc_lfcliff = 0;
		gc_rcliff_amt = gc_rfcliff_amt = gc_lcliff_amt = gc_lfcliff_amt = 0;
		return 0;
	}
	// Create Library: update angle travelled
	public int create_angle() {
		_nc();
		if (!_ic()) return -1;
		// this is a little unfair, as it caters to people who abuse this inaccurate
		//  measure of angle by providing them with the raw simulator headings.
		// keep this in mind when using the simulator.
		gc_angle = gc_total_angle = 0;
		return 0;
	}
	// Create Library: update requested velocity
	public int create_velocity() {
		_nc();
		if (!_ic()) return -1;
		// create velocity is always up to date
		return 0;
	}
	// Create Library: update distance travelled
	public int create_distance() {
		_nc();
		if (!_ic()) return -1;
		// this is a little unfair, as it caters to people who abuse this inaccurate
		//  measure of distance by providing them with the raw simulator output.
		// keep this in mind when using the simulator.
		gc_distance = 0;
		return 0;
	}
	// Create Library: update battery charge
	public int create_battery_charge() {
		_nc();
		if (!_ic()) return -1;
		gc_charge_state = 0;
		gc_batt_voltage = 16500;
		gc_current_flow = 0; gc_batt_temp = 20;
		gc_batt_charge = gc_batt_capacity = 2075;
		return 0;
	}
	// Create Library: update buttons
	public int create_buttons() {
		_nc();
		if (!_ic()) return -1;
		gc_advance_button = gc_play_button = 0;
		return 0;
	}
	// Create Library: update wall and home base
	public int create_wall() {
		_nc();
		if (!_ic()) return -1;
		// This points to the right but is too high to trip on pipes.
		gc_wall = 0;
		gc_wall_amt = 0;
		// never a home base
		gc_wall_hb = 0;
		return 0;
	}
	// Create Library: update remote byte
	public int create_read_IR() {
		_nc();
		if (!_ic()) return -1;
		gc_IR = -1;
		return 0;
	}
	// Create Library: update analog and digital in
	public int create_cargo_bay_inputs() {
		_nc();
		if (!_ic()) return -1;
		gc_digital_in = gc_analog_in = 0;
		return 0;
	}
	// Create Library: stop motors
	public void create_stop() {
		_nc();
		if (_ic()) create_drive(0, 0);
	}
	// Create Library: arc turn at given average velocity with radius in mm
	public void create_drive(int vel, int radius) {
		_nc();
		float angle;
		if (_ic()) {
			gc_vel = vel;
			gc_radius = radius;
			gc_lvel = gc_rvel = 0;
			if (radius >= 32767 || radius <= -32768)
				// Straight
				_gc_l = _gc_r = vel;
			else if (radius == 1) {
				// CCW
				_gc_l = -vel;
				_gc_r = vel;
			} else if (radius == -1) {
				// CW
				_gc_l = vel;
				_gc_r = -vel;
			} else if (radius == 0)
				// stop
				_gc_l = _gc_r = 0;
			// Formula used:
			//  Let vel be the velocity of the faster wheel.
			//  The faster wheel is whichever is farther from center of arc.
			//  Then map that velocity to a slice with center at arc center
			//   with the wheel velocity and position being the outside.
			//  Project the other wheel onto that slice, continuing the slice
			//   beyond the center if needed.
			//  Map the slice radius back to a (possibly negative) velocity.
			//  Assign lesser velocity to the other (slower) wheel.
			else if (radius > 0) {
				// arc towards CCW (left)
				_gc_r = vel;
				angle = (float)vel / (float)(RobotConstants.CREATE_RADIUS + radius);
				_gc_l = Math.round(angle * (float)(radius - RobotConstants.CREATE_RADIUS));
			} else {
				radius = -radius;
				// arc towards CW (right)
				_gc_l = vel;
				angle = (float)vel / (float)(RobotConstants.CREATE_RADIUS + radius);
				_gc_r = Math.round(angle * (float)(radius - RobotConstants.CREATE_RADIUS));
			}
			// If anyone can verify that the Create uses a different formula,
			//  please feel free to substitute.
		}
	}
	// Create Library: drive each wheel at given velocity in mm/sec
	public void create_drive_direct(int l, int r) {
		_nc();
		if (_ic()) {
			gc_vel = gc_radius = 0;
			gc_lvel = l;
			gc_rvel = r;
			_gc_l = l;
			_gc_r = r;
		}
	}
	// Create Library: drives straight at given velocity
	public void create_drive_straight(int v) {
		_nc();
		if (_ic()) create_drive(v, 32767);
	}
	// Create Library: spins clockwise at given velocity
	public void create_spin_CW(int v) {
		_nc();
		if (_ic()) create_drive(v, -1);
	}
	// Create Library: spins counterclockwise at given velocity
	public void create_spin_CCW(int v) {
		_nc();
		if (_ic()) create_drive(v, 1);
	}
	// Create Library: spins and blocks for given ~ number of degrees
	public void create_spin_block(int v, int degrees) {
		_nc();
		if (_ic()) {
			create_drive_direct(-v, v);
			// 16.5 ticks per degree.
			//long targ = Math.abs(degrees) * 165L / 10L;
			// Do NOT fix this to be exact!
			create_stop();
		}
	}
	// Undocumented Create Library: fetch raw encoder values
	public int _create_get_raw_encoders(long lvel, long rvel) {
		return 0;
	}
	// Create Library: turns on or off the advance LED
	public void create_advance_led(int on) {
		_nc();
		if (_ic())
			_gc_leds[2] = on;
	}
	// Create Library: turns on or off the play LED
	public void create_play_led(int on) {
		_nc();
		if (_ic())
			_gc_leds[1] = on;
	}
	// Create Library: sets the brightness and color of power LED
	public void create_power_led(int color, int bright) {
		_nc();
		if (_ic())
			_gc_leds[0] = color + 65536 * bright;
	}
	// Create Library: loads a song into memory
	public void create_load_song(int num) {
		_nc();
		if (_ic()) _bot.print("Simulator doesn't currently support create music\n");
	}
	// Create Library: plays a song
	public void create_play_song(int num) {
		_nc();
		if (_ic()) _bot.print("Simulator doesn't currently support create music\n");
	}
	// END CREATE LIBRARY
	// XBC/CBC Library: moves motor on given port at given velocity in ticks/second
	public void mav(int port, int speed) {
		if (port < 0 || port > 3) return;
		if (speed > 1000)
			speed = 1000;
		else if (speed < -1000)
			speed = -1000;
		motor(port, speed / 10);
		_speed[port] = speed;
	}
	// XBC/CBC Library: moves motor on given port at given velocity to given destination
	public void mrp(int port, int speed, long ticks) {
		if (port < 0 || port > 3) return;
		mtp(port, speed, _counts[port] + ticks);
	}
	// XBC/CBC Library: alias for mrp
	public void move_relative_position(int port, int speed, long ticks) {
		mrp(port, speed, ticks);
	}
	// XBC/CBC Library: moves motor on given port to given absolute destination
	public void mtp(int port, int speed, long ticks) {
		if (ticks == 0) {
			off(port);
			return;
		}
		pid_control(port, speed * (int)Math.signum(ticks -
			get_motor_position_counter(port)), ticks);
	}
	// XBC/CBC Library: alias for mtp
	public void move_to_position(int port, int speed, long ticks) {
		mtp(port, speed, ticks);
	}
	// PID controls motor on given port to the new destination at given speed
	private void pid_control(int port, int speed, long dest) {
		if (port < 0 || port > 3) return;
		mav(port, speed);
		_dest[port] = dest;
		_updateMotor(port);
	}
	// XBC/CBC Library: gets position counter on given motor
	public long get_motor_position_counter(int port) {
		_s();
		if (port < 0 || port > 3) return 0L;
		return _counts[port];
	}
	// XBC/CBC Library: clears position counter on given motor
	public void clear_motor_position_counter(int port) {
		_s();
		if (port < 0 || port > 3) return;
		_counts[port] = 0L;
		_updateMotor(port);
	}
	// HB/XBC/CBC Library: gets requested servo position on given port
	public int get_servo_position(int port) {
		_s();
		if (port < 0 || port > 3) return -1;
		return _pos[port];
	}
	// HB/XBC Library: enables all servos
	public void enable_servos() {
		_s();
		_servos_enabled = true;
		_bot.enableServos();
	}
	// HB/XBC Library: disables all servos
	public void disable_servos() {
		_s();
		_servos_enabled = false;
		_bot.disableServos();
	}
	// HB/XBC/CBC Library: sets servo position on given port
	public void set_servo_position(int port, int pos) {
		_s();
		if (port < 0 || port > 3 || pos < -1 || pos > 2047) return;
		_pos[port] = pos;
		MotorComponent servo = _bot.getServo(port);
		if (pos == -1) servo.servoDisable(); // CBC
		else {
			servo.setDest(pos);
			// behavior displayed by CBC, not any other
			servo.servoEnable();
		}
	}
	// XBC/CBC Library: wait until mrp or mtp is done on given port
	public void bmd(int port) {
		while (!get_motor_done(port)) msleep(1L);
	}
	// XBC/CBC Library: alias for bmd
	public void block_motor_done(int port) {
		bmd(port);
	}
	// XBC/CBC Library: checks if mrp or mtp is done on given port
	public boolean get_motor_done(int port) {
		_s();
		if (port < 0 || port > 3) return false;
		return _speed[port] == 0;
	}
	// RCX/HB/XBC/CBC Library: waits for given number of milliseconds
	public void msleep(long ms) {
		_s();
		long dest = _mseconds() + ms;
		while (_mseconds() < dest) {
			// use defer() or yield()?
			try {
				Thread.sleep(1L);
			} catch (Exception e) { }
			_s();
		}
	}
	// RCX/HB/XBC/CBC Library: yields processor time to other threads
	public void defer() {
		_s();
		Thread.yield();
	}
	// RCX/HB/XBC/CBC Library: waits for given number of seconds
	public void sleep(double seconds) {
		msleep(Math.round(seconds * 1000.));
	}
	// RCX/HB/XBC/CBC Library: returns number of seconds since simulation start
	public float seconds() {
		_s();
		return (float)_mseconds() / 1000.f;
	}
	// Returns number of milliseconds since simulation start 
	private long _mseconds() {
		return System.currentTimeMillis() - _start + _total;
	}
	// RCX/HB/XBC Library: returns number of milliseconds since simulation start
	/*public long mseconds() {
		return _mseconds();
	}*/
	// Controls motors and servos
	public void pid_control_task() {
		int i, diff; long last = _mseconds(), time = last, factor;
		MotorComponent servo;
		while (true) {
			last = time;
			time = _mseconds();
			// stop motors while paused
			while (_sim.isPaused() && !_l()) Thread.yield();
			if (_l()) break;
			for (i = 0; i < 4; i++) {
				if ((_vel[i] > 0 && _counts[i] > _dest[i]) ||
						(_vel[i] < 0 && _counts[i] < _dest[i])) {
					// not always exact but close
					_counts[i] = _dest[i] + (System.currentTimeMillis() % 10L) - 5L;
					// behavior for CBC v1, v2: freeze; XBC and earlier: off
					freeze(i);
					_speed[i] = 0;
				} else if (_vel[i] != 0 && time != last) {
					factor = 250L * _vel[i] / (100L * (time - last));
					// slight variation
					_counts[i] += factor + (System.currentTimeMillis() % 3L) - 1L;
					_updateMotor(i);
				}
				// rotate the appropriate servo at a max rate of 0.2 sec/60 deg, 1.2 rev/s
				servo = _bot.getServo(i);
				if (_servos_enabled && _loc[i] != _pos[i] && time != last) {
					diff = 1000 / (int)(time - last);
					// slew servo to position (always exact, it's an unloaded servo)
					if (_loc[i] < _pos[i])
						_loc[i] = Math.min(_loc[i] + diff, _pos[i]);
					else
						_loc[i] = Math.max(_loc[i] - diff, _pos[i]);
					servo.setPos(_loc[i]);
					servo.setShaftAngle(180 * _loc[i] / 2048 - 90);
				}
			}
			// resolution on CBC varies, XBC is probably locked at around 3L-4L (FPGA)
			try {
				Thread.sleep(4L + System.currentTimeMillis() % 4L);
			} catch (Exception e) { }
		}
	}
	// RCX/HB/XBC/CBC Library: turns off all motors
	public void ao() {
		off(0);
		off(1);
		off(2);
		off(3);
	}
	// RCX/HB/XBC/CBC Library: alias for ao
	public void alloff() {
		ao();
	}
	// RCX/HB/XBC/CBC Library: turns off the given motor
	public void off(int port) {
		motor(port, 0);
	}
	// RCX/HB/XBC/CBC Library: turns on the given motor at given PWM
	public void motor(int port, int speed) {
		_s();
		if (port < 0 || port > 3) return;
		if (speed < -100) speed = -100;
		if (speed > 100) speed = 100;
		if (speed > 0)
			_dest[port] = Long.MAX_VALUE;
		else
			_dest[port] = Long.MIN_VALUE;
		_vel[port] = speed;
		_speed[port] = 0;
		_updateMotor(port);
	}
	// RCX/HB/XBC/CBC Library: sets raw PWM on port
	public void setpwm(int port, int speed) {
		// Approximation
		motor(port, speed * 100 / 255);
	}
	// RCX/HB/XBC/CBC Library: turns motor on in forward direction
	public void fd(int port) {
		motor(port, 100);
	}
	// RCX/HB/XBC/CBC Library: turns motor on in reverse direction
	public void bk(int port) {
		motor(port, -100);
	}
	// XBC/CBC Library: PID freezes motor (not the same as electrical brake)
	public void freeze(int port) {
		if (port < 0 || port > 3) return;
		off(port);
		// get_motor_done is never true if it's frozen
		_speed[port] = 1;
		_dest[port] = _counts[port];
		// approximate acceleration
		_bot.getMotor(port).setPower(500);
		_bot.getMotor(port).setDest(_dest[port]);
		_bot.getMotor(port).setPos(_counts[port]);
	}
	// RCX/HB/XBC/CBC Library: reads digital sensor on the given port
	public boolean digital(int port) {
		_s();
		return false;
	}
	// RCX/HB/XBC/CBC Library: reads analog sensor on the given port
	public int analog(int port) {
		return analog10(port) / 4;
	}
	// CBC Library: reads analog sensor on the given port to 10-bit (1024) precision
	public int analog10(int port) {
		_s();
		return 1022;
	}
	// XBC Library: reads analog sensor to 12-bit (4092) precision
	public int analog12(int port) {
		return 4 * analog10(port);
	}
	// HB/XBC/CBC Library: reads sonar on given port and returns distance in mm
	public int sonar(int port) {
		_s();
		return -32767;
	}
	// RCX/HB/XBC/CBC Library: beeps
	public void beep() {
		tone(500., .07);
		sleep(.03);
	}
	// RCX/HB/XBC Library: plays tone of given frequency for given length in seconds
	public void tone(double freq, double len) {
		set_beeper_pitch(freq);
		beeper_on();
		sleep(len);
		beeper_off();
	}
	// RCX/HB/XBC Library: changes frequency of beeper without power change
	public void set_beeper_pitch(double freq) {
		_s();
	}
	// RCX/HB/XBC Library: turns beeper on
	public void beeper_on() {
		_s();
		_sim.getGraphicsComponent().setBeeperOn();
	}
	// RCX/HB/XBC Library: turns beeper off
	public void beeper_off() {
		_s();
		_sim.getGraphicsComponent().setBeeperOff();
	}
	// HB/XBC/CBC Library: prints message to the screen
	public void printf(String format, Object... args) {
		_s();
		_bot.printf(format, args);
	}
	// CBC Library: prints message to the screen
	public void cbc_printf(int x, int y, String format, Object... args) {
		printf(format, args);
	}
	// HB/XBC Library: clears the screen
	public void display_clear() {
		_s();
		// XBC
		_bot.clearLCD();
	}
	// CBC Library: clears the screen
	public void cbc_display_clear() {
		_s();
		// CBC
		_bot.print("\n\n\n\n\n\n\n\n");
	}
	// XBC/CBC Library: returns status of A button
	public boolean a_button() {
		return (button_mask() & A_BUTTON) > 0;
	}
	// XBC/CBC Library: returns status of B button
	public boolean b_button() {
		return (button_mask() & B_BUTTON) > 0;
	}
	// XBC/CBC Library: returns status of left button
	public boolean left_button() {
		return (button_mask() & LEFT_BUTTON) > 0;
	}
	// XBC/CBC Library: returns status of right button
	public boolean right_button() {
		return (button_mask() & RIGHT_BUTTON) > 0;
	}
	// XBC/CBC Library: returns status of up button
	public boolean up_button() {
		return (button_mask() & UP_BUTTON) > 0;
	}
	// XBC/CBC Library: returns status of down button
	public boolean down_button() {
		return (button_mask() & DOWN_BUTTON) > 0;
	}
	// HB/RCX/XBC/CBC Library: returns status of choose button
	public boolean choose_button() {
		return (button_mask() & CHOOSE_BUTTON) > 0;
	}
	// HB/RCX/XBC/CBC Library: returns status of cancel button
	public boolean cancel_button() {
		return (button_mask() & CANCEL_BUTTON) > 0;
	}
	// CBC Library: returns status of black button
	public boolean black_button() {
		_s();
		return _bot.getBlackButton();
	}
	// XBC Library: Returns status of all buttons
	public int button_mask() {
		_s();
		return _bot.buttonMask();
	}
	// HB/RCX/XBC/CBC Library: cosine of angle in radians
	public float cos(double angle) {
		_s();
		return (float)Math.cos(angle);
	}
	// HB/RCX/XBC/CBC Library: sine of angle in radians
	public float sin(double angle) {
		_s();
		return (float)Math.sin(angle);
	}
	// HB/RCX/XBC/CBC Library: tangent of angle in radians
	public float tan(double angle) {
		_s();
		return (float)Math.tan(angle);
	}
	// HB/RCX/XBC/CBC Library: arctangent of angle in radians
	public float atan(double angle) {
		_s();
		return (float)Math.atan(angle);
	}
	// HB/RCX/XBC/CBC Library: 10^x
	public float exp10(double power) {
		_s();
		return (float)Math.pow(10, power);
	}
	// HB/RCX/XBC/CBC Library: e^x
	public float exp(double power) {
		_s();
		return (float)Math.exp(power);
	}
	// HB/RCX/XBC/CBC Library: ln(x)
	public float log(double x) {
		_s();
		return (float)Math.log(x);
	}
	// HB/RCX/XBC/CBC Library: log base 10 of x
	public float log10(double x) {
		_s();
		return (float)Math.log10(x);
	}
	// HB/RCX/XBC/CBC Library: square root of x
	public float sqrt(double x) {
		_s();
		return (float)Math.sqrt(x);
	}
	// HB/RCX/XBC/CBC Library: battery voltage in volts
	public float power_level() {
		_s();
		return 9.f;
	}
	// HB/RCX/XBC/CBC Library: random number from 0 to 65535
	public int random() {
		return random(Short.MAX_VALUE);
	}
	// HB/RCX/XBC/CBC Library: random number from 0 to i
	public int random(int i) {
		_s();
		return (int)(i * Math.random());
	}
	// CBC[2] Library: sets floating or non floating analog states
	public void set_each_analog_state(int p0, int p1, int p2, int p3, int p4,
			int p5, int p6, int p7) {
		_s();
		// Simulator auto manages analog states
	}
	// HB/RCX/XBC/CBC Library: starts named user function as a process
	public int start_process(String fn) {
		_s();
		UserThread t = new UserThread(_nextID, fn);
		t.setName("User Thread #" + _nextID + " (" + fn + ")");
		t.start();
		_threads.add(t);
		return _nextID++;
	}
	// HB/RCX/XBC/CBC Library: kills the user process with given ID
	public int kill_process(int id) {
		_s();
		int nid; UserThread t;
		Iterator<UserThread> it = _threads.iterator();
		// Note: only will properly kill when Botball function is next called
		//  while (1);     will not terminate
		while (it.hasNext()) {
			t = it.next();
			nid = t.getID();
			if (nid == id) {
				t.kill();
				it.remove();
				return 1;
			}
		}
		//printf("Process %d not found\n", id);
		return 0;
	}
	// TODO: use this function to fix up boolean/int confusion in re-parse
	public boolean test(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Integer)
			return (Integer)obj != 0;
		if (obj instanceof Long)
			return (Long)obj != 0L;
		if (obj instanceof Double)
			return (Double)obj != 0.;
		if (obj instanceof Float)
			return (Float)obj != 0.f;
		if (obj instanceof Byte)
			return (Byte)obj != 0;
		if (obj instanceof Short)
			return (Short)obj != 0;
		if (obj instanceof Character)
			return (Character)obj != '\u0000';
		return true;
	}
	// HB/RCX/XBC/CBC Library: calibrate light sensor to starting light on given port
	public void wait_for_light(int port) {
		while (true) {
			// most controllers use analog
			//  CBC2 uses analog10
			printf("Calibrate with sensor on port %d\n", port);
			msleep(500L);
			// XBC uses L and R
			printf("Press Left when light on\n");
			while (!left_button()) defer();
			int li = analog(port);
			beep();
			printf("Light on value is %d\n", li);
			msleep(500L);
			beep();
			printf("Press Right when light off\n");
			while (!right_button()) defer();
			int lo = analog(port);
			beep();
			printf("Light off value is %d\n", lo);
			msleep(500L);
			int d = lo - li, center = (lo + li) / 2;
			// value from XBC library
			if (d > 128) {
				printf("Good Calibration\nDifference is %d\nWaiting...", d);
				beep();
				while (analog(port) > center) defer();
				return;
			} else {
				beep();
				printf("Bad Calibration ");
				if (li > 128)
					printf("Aim Sensor!\n");
				else
					printf("Add Shielding!\n");
				msleep(500L);
			}
		}
	}
	// HB/RCX/XBC/CBC Library: shuts down after given time
	public void shut_down_in(double time) {
		_shutdown = (float)time;
		printf("shut_down_in %f\n", _shutdown);
		start_process("shut_down_task");
	}
	// HB/RCX/XBC Library: Used in some libraries to block user threads
	public void hog_processor() { }
	// Shuts down controller as a separate process
	public void shut_down_task() {
		sleep(_shutdown);
		printf("Game over");
		// kill all threads that are not this one
		for (UserThread t : _threads)
			if (!t.equals(Thread.currentThread())) t.kill();
		ao();
		beeper_off();
		disable_servos();
		// disconnect create
		if (g_create_connected == 1) {
			create_stop();
			create_passive();
			create_disconnect();
		}
		printf(".\n");
	}
	// CBC Library: runs function for the given time period
	//  and kills it if it does not stop
	public void run_for(String fn, double time) {
		int id = start_process(fn);
		float then = seconds() + (float)time;
		while (seconds() < then) {
			for (UserThread t : _threads)
				if (t.getID() == id && !t.isAlive())
					return;
			msleep(100L);
		}
		kill_process(id);
	}
	// HB/RCX/XBC/CBC Library: for KISS sim compatibility
	public void kissSimEnablePause() {
		_s();
	}
	// HB/RCX/XBC/CBC Library: pauses simulator now
	public void kissSimPause() {
		_s();
		_bot.pause();
	}

	// Returns create LED status
	int[] _createLEDs() {
		return _gc_leds;
	}
	// Returns create left drive motor speed
	int _createLeft() {
		return _gc_l;
	}
	// Returns create right drive motor speed
	int _createRight() {
		return _gc_r;
	}
	// Returns whether global servos are enabled
	//  for CBC, individual servos might have different status
	boolean _servosEnabled() {
		return _servos_enabled;
	}
	// Sets parent simulator and robot
	void _setSim(Simulator sim, SimRobot bot) {
		_sim = sim;
		_bot = bot;
	}
	// Asks the simulator to update the motor status on the given port
	void _updateMotor(int port) {
		_bot.getMotor(port).setPower(_vel[port]);
		_bot.getMotor(port).setShaftAngle((int)((360L * _counts[port] / 1300L) % 360L));
		_bot.getMotor(port).setDest(_dest[port]);
		_bot.getMotor(port).setPos(_counts[port]);
	}
	// Timing methods to handle pauses correctly with mseconds()
	void _stopTiming() {
		_total = _mseconds();
	}
	void _startTiming() {
		_start = System.currentTimeMillis();
	}
	// Calls the main method of the program and starts up robot control
	void invokeMain() {
		_killAll();
		_threads.clear();
		while (_sim.isPaused()) Thread.yield();
		_start = System.currentTimeMillis();
		_total = 0L;
		start_process("main");
		start_pid();
	}
	// Starts PID control task for motors, servos
	void start_pid() {
		if (pidTask == null) {
			pidTask = new UserThread(-1, "pid_control_task");
			pidTask.setName("PID control task");
			pidTask.start();
		}
	}
	// Checks to see if program is still running
	boolean _isRunning() {
		for (UserThread t : _threads)
			if (t.isAlive())
				return true;
		_threads.clear();
		if (pidTask != null) {
			// done, close down PID
			pidTask.kill();
			pidTask = null;
		}
		return false;
	}
	// Forcefully kills the program
	void _killAll() {
		for (UserThread t : _threads)
			t.kill();
		_threads.clear();
		if (pidTask != null) {
			pidTask.kill();
			pidTask = null;
		}
	}
	// Calls the given method
	private void _invoke(String name) throws Exception {
		Method[] methods = getClass().getDeclaredMethods();
		for (Method myMethod : methods)
			if (myMethod.getName().equals(name)) {
				myMethod.setAccessible(true);
				myMethod.invoke(this, new Object[] { });
				return;
			}
		methods = getClass().getMethods();
		for (Method myMethod : methods)
			if (myMethod.getName().equals(name)) {
				myMethod.setAccessible(true);
				myMethod.invoke(this, new Object[] { });
				return;
			}
		throw new NoSuchMethodException(name);
	}
	// Handles state if simulator was paused or killed
	//  called by all library functions to enable universal pause
	private void _s() {
		if (_l()) throw new Killed();
		while (_sim.isPaused()) try {
			Thread.sleep(1L);
		} catch (Exception e) {
			if (_l()) throw new Killed();
		}
	}
	// Checks to see if simulator was killed
	private boolean _l() {
		Thread t = Thread.currentThread();
		if (t instanceof UserThread) {
			UserThread ut = (UserThread)t;
			return ut._killcheck();
		}
		return false;
	}

	/**
	 * A class representing a thread executing user code.
	 * 
	 * @author Stephen Carlson
	 */
	private class UserThread extends Thread {
		// Function name of called code
		private String fn;
		// Thread ID
		private int id;
		// used to stop the thread
		private volatile boolean killme;

		/**
		 * Creates a new user thread with the given ID # and function.
		 * 
		 * @param id the ID # from start_process
		 * @param fn the function to call
		 */
		public UserThread(int id, String fn) {
			this.fn = fn;
			this.id = id;
			synchronized (fn) {
				killme = false;
			}
		}
		/**
		 * Gets the thread ID #.
		 * 
		 * @return the ID
		 */
		public int getID() {
			return id;
		}
		public void run() {
			try {
				// call user function
				_invoke(fn);
			} catch (NoSuchMethodError e) {
			} catch (Throwable e) {
				if (e.getCause() != null) e = e.getCause();
				if (e instanceof Killed) return;
				// TODO match the processor-specific error messages
				_bot.printf("Run-time Error - " + e.getClass().getSimpleName() + "\n");
			}
		}
		/**
		 * Checks to see if the thread was killed.
		 * 
		 * @return whether the thread was killed
		 */
		private boolean _killcheck() {
			synchronized (fn) {
				return killme;
			}
		}
		/**
		 * Kills the thread; it will die on next _s() call.
		 */
		public void kill() {
			synchronized (fn) {
				killme = true;
			}
			this.interrupt();
		}
	}

	/**
	 * Exception thrown when thread is killed.
	 *  Could use ThreadDeath but it is evidently used in JRE 6+ (and not before).
	 */
	private class Killed extends Error {
		private static final long serialVersionUID = 0L;

		public Killed() {
			super("Thread killed with stop or kill_process");
		}
	}
}