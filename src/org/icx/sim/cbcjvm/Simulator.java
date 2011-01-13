/*
 * This file is part of CBCJVM.
 * CBCJVM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CBCJVM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CBCJVM.  If not, see <http://www.gnu.org/licenses/>.
 */

package cbccore.low;

import cbccore.Device;

/**
 * A parent class for any simulator. The goal is to provide an easy way to write
 * or link a simulator with CBCJVM.
 *
 * @author Benjamin Woodruff
 *
 */

public class Simulator {
	private Sound sound;
	private Sensor sensor;
	private cbccore.low.Device device;
	private Display display;
	private Input input;
	private Servo servo;
	private Motor motor;
	private Camera camera;
	private Create create;
	
	public Simulator(Sound sound, Sensor sensor, cbccore.low.Device device,
	                 Display display, Input input, Servo servo, Motor motor,
	                 Camera camera, Create create) {
		setSound(sound);
		setSensor(sensor);
		setDevice(device);
		setDisplay(display);
		setInput(input);
		setServo(servo);
		setMotor(motor);
		setCamera(camera);
		setCreate(create);
	}
	
	protected void setSound(Sound sound) {
		this.sound = sound;
	}
	
	public Sound getSound() {
		return sound;
	}
	
	protected void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
	public Sensor getSensor() {
		return sensor;
	}
	
	protected void setDevice(cbccore.low.Device device) {
		this.device = device;
	}
	
	public cbccore.low.Device getDevice() {
		return device;
	}
	
	protected void setDisplay(Display display) {
		this.display = display;
	}
	
	public Display getDisplay() {
		return display;
	}
	
	protected void setInput(Input input) {
		this.input = input;
	}
	
	public Input getInput() {
		return input;
	}
	
	protected void setServo(Servo servo) {
		this.servo = servo;
	}
	
	public Servo getServo() {
		return servo;
	}
	
	protected void setMotor(Motor motor) {
		this.motor = motor;
	}
	
	public Motor getMotor() {
		return motor;
	}
	
	protected void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	protected void setCreate(Create create) {
		this.create = create;
	}
	
	public Create getCreate() {
		return create;
	}
	
	// TODO: this method should be overhauled to make things more simpler
	public void addFramebuffers() {
		// overrideable stub
	}
}
