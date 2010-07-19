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

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;

/**
 * The program that displays the simulation. 
 * 
 * @author Stephen Carlson
 */
public class Simulator extends JFrame implements Runnable {
	// all images are sourced as /images/name.ext
	private static final String imageExtension = ".png";
	private static final long serialVersionUID = 0L;

	// The place where everything is displayed.
	private GraphicsComponent gc;
	// All simulated items excluding robots.
	private Environment env;
	// The play/pause button.
	private JButton pp;
	// The Hand of God button.
	private JButton god;
	// Starting Light.
	private JButton light;
	// Whether the simulator is paused.
	private boolean pause;
	// When using Hand of God, indicates whether program was paused before.
	private boolean wasPaused;
	// "LCD" screen
	private JTextArea lcd;
	// Digital inputs implemented as lock down buttons
	private LockingButton[] digitals;
	// Controller buttons - these are really toggles
	//  so to press and release A, type 'a' or click "A" twice
	private JButton[] buttons;
	// The analog inputs as sliders.
	//  Stores no information about real inputs!
	private AnalogSlider[] analogs;
	// Listens for program-wide events.
	private EventListener events;
	// Icons for pause and play.
	private Icon playIcon;
	private Icon pauseIcon;
	// Delegate writer for the LCD.
	private PrintWriter lcdWriter;
	private ClearableStringWriter str;
	// The dialog box shown when "Load(ing) Code"
	private JFileChooser jf;
	// Motors and servos as displayed on screen
	private MotorComponent[] motors;
	private MotorComponent[] servos;
	// Loader that creates the Botball program
	private ICClassLoader icLoader;
	// The current program (currently only one)
	private BotballProgram instance;
	// Sensor configuration window
	private JDialog sensorSetup;
	// The names of the installed sensors on the screen
	private JLabel[] sensorNames;

	/**
	 * Creates a Simulator but does not start it.
	 */
	public Simulator() {
		super("Botball Simulator");
		setupUI();
		icLoader = new ICClassLoader();
		playIcon = getIcon("play");
		pauseIcon = getIcon("pause");
		str = new ClearableStringWriter();
		lcdWriter = new PrintWriter(str);
		env = new Environment();
		setPP(true);
	}

	/**
	 * Gets the LCD writer.
	 * 
	 * @return a PrintWriter which will write to the LCD screen
	 */
	public PrintWriter getLCDWriter() {
		return lcdWriter;
	}

	/**
	 * Enables analog type changes.
	 */
	private void enableAnalogs() {
		for (int i = 0; i < analogs.length; i++)
			analogs[i].enableType();
	}

	/**
	 * Disables analog type changes.
	 */
	private void disableAnalogs() {
		for (int i = 0; i < analogs.length; i++)
			analogs[i].disableType();
	}

	/**
	 * Prints the text to the LCD, and auto flushes.
	 * 
	 * @param text the text to print
	 */
	public synchronized void print(String text) {
		print(text, true);
	}

	/**
	 * Prints the text to the LCD, possibly refreshing.
	 * 
	 * @param text the text to print
	 * @param flush whether the LCD should be refreshed
	 */
	public synchronized void print(String text, boolean flush) {
		lcdWriter.print(text);
		if (flush) refreshLCD();
	}

	/**
	 * Gets the simulation environment.
	 * 
	 * @return the environment of robots and objects
	 */
	public Environment getEnvironment() {
		return env;
	}

	/**
	 * Refreshes the LCD screen.
	 */
	public synchronized void refreshLCD() {
		lcd.setText(str.toString());
		try {
			lcd.setCaretPosition(str.length());
		} catch (Exception e) {
			// This exception was caused by a sync problem in SimRobot.java
			//  Basically, printf() was using format() and then refresh(), but
			//  multi threading printf() could cause bad positions.
			//  That has been fixed, but if it reappears, it will show up here.
			e.printStackTrace();
		}
	}

	/**
	 * Clears the LCD screen.
	 */
	public synchronized void clearLCD() {
		lcd.setText("");
		str.clear();
	}

	// Sets up the user interface
	private void setupUI() {
		setupInitUI();
		setupTopUI();
		setupMiddleUI();
		setupRightUI();
		setupSensorUI();
		setupExtraUI();
	}

	// Performs UI initialization.
	private void setupInitUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }
		// initialize
		events = new EventListener();
		motors = new MotorComponent[4];
		servos = new MotorComponent[4];
		gc = new GraphicsComponent(this, 3000, 3000);
		gc.addKeyListener(events);
		getContentPane().setLayout(new BorderLayout());
	}

	// Sets up extra extensions to the UI.
	private void setupExtraUI() {
		// final words
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 750);
		addKeyListener(events);
		gc.addMouseListener(events);
		gc.addMouseMotionListener(events);
		// load code dialog
		jf = new JFileChooser();
		jf.setDialogType(JFileChooser.OPEN_DIALOG);
		jf.setDialogTitle("Select KISS-C/IC File to Load");
		jf.setFileFilter(new IKCFileFilter());
		jf.setFileHidingEnabled(true);
		jf.setCurrentDirectory(new File("."));
	}

	// Initializes sensor extensions.
	private void setupSensorUI() {
		sensorSetup = new JDialog(this, "Sensor Setup", true);
		sensorSetup.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		sensorSetup.setResizable(false);
		// create 15 row dialog box
		Container c = sensorSetup.getContentPane();
		c.setLayout(new VerticalFlow(true));
		JLabel name; JButton setup; JComponent across;
		sensorNames = new JLabel[15];
		c.add(Box.createVerticalStrut(2));
		for (int i = 0; i < sensorNames.length; i++) {
			// row label
			across = new Box(BoxLayout.X_AXIS);
			across.add(Box.createHorizontalStrut(2));
			name = new JLabel(i + ": ");
			name.setFont(name.getFont().deriveFont(Font.BOLD, 14.f));
			across.add(name);
			// make array of sensors
			name = new JLabel("No Sensor");
			name.setFont(name.getFont().deriveFont(14.f));
			sensorNames[i] = name;
			across.add(name);
			across.add(Box.createHorizontalGlue());
			// configure button
			setup = new JButton("Configure...");
			setup.setActionCommand("conf" + i);
			setup.addActionListener(events);
			setup.setFocusable(false);
			across.add(setup);
			across.add(Box.createHorizontalStrut(2));
			c.add(across);
			c.add(Box.createVerticalStrut(2));
		}
		sensorSetup.pack();
		sensorSetup.setSize(400, sensorSetup.getHeight());
	}

	// Updates the sensors in the dialog box to reflect changes.
	protected void updateSensors() {
		SimRobot bot = env.getFirstRobot();
		Sensor[] sensors = bot.getSetup().getSensors();
		for (int i = 0; i < sensors.length; i++) {
			// change names
			if (sensors[i] == null)
				sensorNames[i].setText("No Sensor");
			else
				sensorNames[i].setText(sensors[i].getName());
		}
	}

	// Initializes the top of the window's UI.
	private void setupTopUI() {
		JComponent controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JComponent vert = new Box(BoxLayout.Y_AXIS);
		JComponent across = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
		// play/pause button
		pp = new JButton(playIcon);
		pp.setBorder(BorderFactory.createEmptyBorder());
		pp.setContentAreaFilled(false);
		pp.setActionCommand("play");
		pp.addActionListener(events);
		pp.setFocusable(false);
		// e-stop
		JButton stop = new JButton(getIcon("stop"));
		stop.setBorder(BorderFactory.createEmptyBorder());
		stop.setContentAreaFilled(false);
		stop.setActionCommand("stop");
		stop.addActionListener(events);
		stop.setFocusable(false);
		across.add(pp);
		across.add(stop);
		vert.add(across);
		// load code and hand of god
		JButton load = new JButton("Load Code");
		load.setActionCommand("load");
		load.setFocusable(false);
		load.addActionListener(events);
		load.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		god = new JButton("Hand of God");
		god.setActionCommand("god");
		god.setFocusable(false);
		god.addActionListener(events);
		god.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		// lay out with play/e-stop on top, then load code, then hand of god
		vert.add(load);
		vert.add(Box.createVerticalStrut(3));
		vert.add(god);
		vert.add(Box.createVerticalStrut(3));
		controls.add(vert);
		controls.add(Box.createHorizontalStrut(20));
		// initialize motors
		for (int i = 0; i < 4; i++) {
			motors[i] = new MotorComponent("M" + i);
			controls.add(motors[i]);
			if (i < 3) controls.add(Box.createHorizontalStrut(5));
		}
		ao();
		controls.add(Box.createHorizontalStrut(20));
		// initialize servos
		for (int i = 0; i < 4; i++) {
			servos[i] = new MotorComponent("S" + i);
			servos[i].setPos(1023);
			servos[i].setDest(1023);
			servos[i].servoDisable();
			controls.add(servos[i]);
			if (i < 3) controls.add(Box.createHorizontalStrut(5));
		}
		getContentPane().add(controls, BorderLayout.NORTH);
	}

	// Initializes the simulation area and controls below the main window.
	private void setupMiddleUI() {
		// graphics component is scrollable
		JComponent center = new JPanel(new BorderLayout(3, 3));
		JScrollPane p = new JScrollPane(gc);
		p.setBackground(Color.WHITE);
		p.setBorder(BorderFactory.createEmptyBorder());
		JComponent vert = new Box(BoxLayout.Y_AXIS);
		// start light, zoom, set up sensors
		JComponent controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JButton setUp = new JButton("Set up Sensors");
		setUp.setFocusable(false);
		setUp.setActionCommand("setup");
		setUp.addActionListener(events);
		controls.add(setUp);
		controls.add(Box.createHorizontalStrut(20));
		light = new JButton("Start Light");
		light.setFocusable(false);
		light.setActionCommand("light");
		light.addActionListener(events);
		controls.add(light);
		controls.add(Box.createHorizontalStrut(20));
		JLabel lbl = new JLabel("Zoom");
		controls.add(lbl);
		// zoom in and out
		JButton zIn = new JButton("+");
		zIn.setFocusable(false);
		zIn.setActionCommand("in");
		zIn.addActionListener(events);
		controls.add(zIn);
		JButton zOut = new JButton("-");
		zOut.setFocusable(false);
		zOut.setActionCommand("out");
		zOut.addActionListener(events);
		controls.add(zOut);
		vert.add(controls);
		// controls for buttons and digitals
		controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		lbl = new JLabel("Buttons");
		lbl.setFont(lbl.getFont().deriveFont(16.0f));
		buttons = new JButton[] {
			new JButton("A"),
			new JButton("B"),
			new JButton(getIcon("up")),
			new JButton(getIcon("down")),
			new JButton(getIcon("left")),
			new JButton(getIcon("right")),
			new JButton("Black")
		};
		// name the buttons with tool tips
		buttons[0].setToolTipText("Choose");
		buttons[1].setToolTipText("Cancel");
		buttons[2].setToolTipText("Up");
		buttons[3].setToolTipText("Down");
		buttons[4].setToolTipText("Left");
		buttons[5].setToolTipText("Right");
		buttons[6].setToolTipText("Black");
		// intialize controller buttons
		controls.add(lbl);
		Font font = lbl.getFont().deriveFont(14.0f);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setFocusable(false);
			buttons[i].setFont(font);
			buttons[i].setActionCommand(buttons[i].getToolTipText().toLowerCase());
			buttons[i].addActionListener(events);
			controls.add(buttons[i]);
		}
		vert.add(controls);
		// digital panel
		controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
		lbl = new JLabel("Digital");
		lbl.setFont(lbl.getFont().deriveFont(16.0f));
		controls.add(Box.createHorizontalStrut(5));
		controls.add(lbl);
		// intialize digital in
		digitals = new LockingButton[8];
		for (int i = 0; i < 8; i++) {
			digitals[i] = new LockingButton(Integer.toString(i + 8));
			controls.add(digitals[i]);
		}
		vert.add(controls);
		center.add(vert, BorderLayout.SOUTH);
		// controls below simulation area
		center.add(p, BorderLayout.CENTER);
		getContentPane().add(center, BorderLayout.CENTER);
	}

	// Initializes the controls on the right side.
	private void setupRightUI() {
		JComponent vert = new JPanel(new VerticalFlow(false));
		JLabel lbl = new JLabel("Analog");
		lbl.setFont(lbl.getFont().deriveFont(16.0f));
		lbl.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		vert.add(lbl);
		// initialize analog in
		analogs = new AnalogSlider[11];
		for (int i = 0; i < 8; i++) {
			analogs[i] = new AnalogSlider(Integer.toString(i));
			vert.add(analogs[i]);
		}
		// accelerometer?
		analogs[8] = new AnalogSlider("AX");
		analogs[8].setValueType(1);
		analogs[8].setValue(512);
		analogs[9] = new AnalogSlider("AY");
		analogs[9].setValueType(1);
		analogs[9].setValue(512);
		analogs[10] = new AnalogSlider("AZ");
		analogs[10].setValue(512 + 128);
		analogs[10].setValueType(1);
		vert.add(analogs[8]);
		vert.add(analogs[9]);
		vert.add(analogs[10]);
		// LCD screen
		lcd = new JTextArea(8, 30);
		lcd.setFocusable(false);
		lcd.setEditable(false);
		lcd.setFont(new Font("Arial", Font.PLAIN, 11));
		JScrollPane p = new JScrollPane(lcd);
		p.setBorder(BorderFactory.createEtchedBorder());
		vert.add(p);
		getContentPane().add(vert, BorderLayout.EAST);
	}

	/**
	 * Gets the graphics component for display.
	 * 
	 * @return the display component
	 */
	public GraphicsComponent getGraphicsComponent() {
		return gc;
	}

	/**
	 * Starts the program.
	 */
	public void start() {
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((ss.width - getWidth()) / 2, (ss.height - getHeight()) / 2);
		setVisible(true);
		requestFocus();
		requestFocus();
		new SimThread().start();
	}

	/**
	 * Returns whether the simulator is paused.
	 * 
	 * @return pause status
	 */
	public boolean isPaused() {
		return pause;
	}

	/**
	 * Disables all motors.
	 *  Meant for reset. Do not change BotballProgram.ao() to call this!
	 */
	public synchronized void ao() {
		for (int i = 0; i < 4; i++) {
			motors[i].setPower(0);
			motors[i].setDest(Long.MAX_VALUE);
		}
	}

	/**
	 * Enables all servos.
	 */
	public synchronized void enableServos() {
		for (int i = 0; i < 4; i++)
			servos[i].servoEnable();
	}

	/**
	 * Disables all servos.
	 */
	public synchronized void disableServos() {
		for (int i = 0; i < 4; i++)
			servos[i].servoDisable();
	}

	/**
	 * Gets the black button status.
	 * 
	 * @return whether the black button is pushed
	 */
	public boolean getBlackButton() {
		return buttons[6].isSelected();
	}

	/**
	 * Adds an object to the simulation (wall, static, etc.)
	 * 
	 * @param obj the object to add
	 */
	public void add(StaticObject obj) {
		env.add(obj);
		gc.add(obj);
	}

	/**
	 * Deletes an object from the simulation (wall, static, etc.)
	 *  No delete robot with this method!!!
	 * 
	 * @param obj the object to remove
	 */
	public void remove(StaticObject obj) {
		env.remove(obj);
		gc.remove(obj);
	}

	/**
	 * Returns an XBC style button mask of controller buttons.
	 * 
	 * @return the button mask
	 */
	public int buttonMask() {
		int mask = 0;
		if (buttons[0].isSelected()) mask |= BotballProgram.A_BTN;
		if (buttons[1].isSelected()) mask |= BotballProgram.B_BTN;
		if (buttons[2].isSelected()) mask |= BotballProgram.UP_BTN;
		if (buttons[3].isSelected()) mask |= BotballProgram.DOWN_BTN;
		if (buttons[4].isSelected()) mask |= BotballProgram.LEFT_BTN;
		if (buttons[5].isSelected()) mask |= BotballProgram.RIGHT_BTN;
		return mask;
	}

	/**
	 * Gets the specified servo.
	 * 
	 * @param port the port number
	 * @return the graphical servo
	 */
	public MotorComponent getServo(int port) {
		return servos[port];
	}

	/**
	 * Gets the specified motor.
	 * 
	 * @param port the port number
	 * @return the graphical motor
	 */
	public MotorComponent getMotor(int port) {
		return motors[port];
	}

	/**
	 * Gets the specified analog slider.
	 * 
	 * @param port the analog port to fetch
	 * @return the value
	 */
	public AnalogSlider getAnalog(int port) {
		return analogs[port];
	}

	/**
	 * Gets the specified digital button.
	 * 
	 * @param port the digital port to fetch
	 * @return the value
	 */
	public LockingButton getDigital(int port) {
		return digitals[port - 8];
	}

	// Loads the given icon from the JAR/file system
	protected static final ImageIcon getIcon(String name) {
		URL url = Simulator.class.getResource("/images/" + name + imageExtension);
		ImageIcon icon;
		if (url == null)
			try {
				icon = new ImageIcon("images/" + name + imageExtension);
				// causes an error if it is null
				if (icon.getImageLoadStatus() != MediaTracker.COMPLETE)
					throw new IOException("Image failed to load");
				return icon;
			} catch (Exception e) {
				e.printStackTrace(System.out);
				die("Could not find a required image.");
				return null;
			}
		else try {
			icon = new ImageIcon(url);
			// causes an error if it is null
			if (icon.getImageLoadStatus() != MediaTracker.COMPLETE)
				throw new IOException("Image failed to load");
			return icon;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			die("Could not find a required image.");
			return null;
		}
	}
	/**
	 * Kills the simulator with an error. Too bad.
	 * 
	 * @param message the error message
	 */
	public static final void die(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

	// Sets the play/pause status button appropriately
	protected void setPP(boolean newStatus) {
		if (newStatus) {
			pp.setIcon(playIcon);
			enableAnalogs();
		} else {
			pp.setIcon(pauseIcon);
			disableAnalogs();
		}
		// manage timing
		if (instance != null && instance._isRunning())
			if (newStatus)
				instance._stopTiming();
			else
				instance._startTiming();
		pause = newStatus;
	}

	/**
	 * Adds a robot to the simulation. Currently, only the first robot will ever
	 *  be used (single support). Fixing this wil not be overly difficult, but
	 *  will require multiple windows/tabs for all of the inputs.
	 * 
	 * @param r the robot to add
	 */
	public void addRobot(SimRobot r) {
		gc.add(r);
		env.addRobot(r);
	}

	/**
	 * Removes a robot from the simulation.
	 * 
	 * @param r the robot to remove
	 */
	public void removeRobot(SimRobot r) {
		gc.remove(r);
		env.removeRobot(r);
	}

	/**
	 * Changes the tint color of all robots.
	 * 
	 * @param color the new color to shade robots
	 */
	public void tintAllRobots(Color color) {
		for (SimRobot r : env.getRobots())
			r.setTint(color);
	}

	// Loads code into the simulator.
	private void loadCode() {
		if (god.isSelected()) return;
		eStop();
		if (jf.showDialog(this, "Load") != JFileChooser.APPROVE_OPTION) return;
		File what = jf.getSelectedFile();
		if (what == null || !what.canRead()) return;
		clearLCD();
		// start compiling (CBC like message)
		print("Compiling /mnt/user/code/test/test.c\n");
		instance = null;
		Thread compile = new Thread(this);
		compile.setPriority(Thread.MAX_PRIORITY - 1);
		compile.setName("User Code Compiler");
		compile.start();
	}

	/**
	 * Pauses the simulator.
	 */
	public synchronized void pause() {
		setPP(true);
	}

	/**
	 * Emergency stops the robot and program.
	 */
	public void eStop() {
		if (instance != null && instance._isRunning()) {
			// stop program
			instance._killAll();
			print("Program Stopped!\n");
		}
		// set pause
		setPP(true);
		// kill motors/servos
		disableServos();
		ao();
		if (instance != null && instance.gc_mode != 0)
			instance.create_disconnect();
		// can't hurt
		for (SimRobot bot : env.getRobots())
			bot.setSpeeds(0, 0);
	}

	/**
	 * Runs or pauses the program.
	 */
	public void play() {
		if (instance == null) return;
		// update status
		setPP(!pause);
		if (!instance._isRunning() && !pause) {
			// set up all robots for the run
			for (SimRobot bot : env.getRobots())
				bot.reset();
			clearLCD();
			// run user code
			Thread pRun = new Thread(Simulator.this);
			pRun.setPriority(Thread.MAX_PRIORITY - 1);
			pRun.setName("User Code Run");
			pRun.start();
		}
	}

	/**
	 * Changes the zoom.
	 * 
	 * @param amount the amount to modify the zoom
	 */
	public void zoom(int amount) {
		int zoom = gc.getZoom();
		zoom = Math.max(-3, Math.min(6, zoom + amount));
		gc.setZoom(zoom);
		validate();
	}

	/**
	 * Toggles the starting light.
	 */
	public void toggleLight() {
		env.setLight(!env.getStartingLight());
		light.setSelected(env.getStartingLight());
	}

	/**
	 * Sets up sensors.
	 */
	public void setUpSensors() {
		updateSensors();
		Rectangle ws = getBounds();
		sensorSetup.setLocation((ws.width - sensorSetup.getWidth()) / 2 + ws.x,
			(ws.height - sensorSetup.getHeight()) / 2 + ws.y);
		sensorSetup.setVisible(true);
	}

	// Sets up a particular sensor.
	protected void setUpSensor(int port) {
		if (port < 0 || port >= sensorNames.length) return;
		Sensor sense = Sensor.getSensor(this);
		if (sense != null) {
			print("Sensor installed on port " + port + "\n");
			analogs[port].setValueType(1);
			env.getFirstRobot().getSetup().getSensors()[port] = sense;
			updateSensors();
		}
	}

	/**
	 * Activates or deactives the Hand of God button.
	 */
	public void handOfGod() {
		if (god.isSelected()) {
			setPP(wasPaused);
			// turn off hand of god
			pp.setEnabled(true);
			tintAllRobots(null);
			god.setSelected(false);
			god.setText("Hand of God");
		} else {
			wasPaused = pause;
			// turn on hand of god
			setPP(true);
			pp.setEnabled(false);
			tintAllRobots(Color.YELLOW);
			god.setSelected(true);
			god.setText("Resume Trial");
		}
	}

	/**
	 * Compiles or runs user code.
	 */
	public void run() {
		if (instance == null) try {
			pause();
			pp.setEnabled(false);
			// call up code parsing
			Reader r = new FileReader(jf.getSelectedFile());
			CodeParser.syntax(jf.getSelectedFile(), r);
			r.close();
			// compile user code
			int res = com.sun.tools.javac.Main.compile(new String[] { "Program.java" },
				getLCDWriter());
			if (res == 0) {
				// load into memory
				Class<?> program = icLoader.loadClass("Program");
				instance = (BotballProgram)program.newInstance();
				instance._setSim(this, env.getFirstRobot());
				// moved down to avoid dup message if loading fails
				print("Compile succeeded.\n");
			} else {
				print("Compile failed.\n");
				instance = null;
			}
			pp.setEnabled(true);
			new File("Program.class").delete();
		} catch (Exception e) {
			// oh no!
			if (e.getMessage() != null)
				print(e.getMessage() + "\n");
			print("Compile failed!\n");
			instance = null;
			pp.setEnabled(true);
		} else
			instance.invokeMain();
	}

	/**
	 * Class which refreshes the screen and runs important sim tasks.
	 */
	private class SimThread extends Thread {
		public SimThread() {
			super("Simulation Thread");
		}
		public void run() {
			long lastRepaint = 0L, lastMove = 0L, lastUpdate = 0L, time;
			while (true) {
				time = System.currentTimeMillis();
				if (time - lastRepaint >= 33L) {
					// handle 33ms tasks
					lastRepaint = time;
					gc.repaint();
					// change icon when program ends
					if (instance != null && !instance._isRunning() && !pause)
						setPP(true);
				}
				if (time - lastUpdate >= 100L) {
					// handle 100ms tasks (update sensor displays)
					// NOTE: There will be a discrepancy between displayed values
					//  and analog() values in code for many sensors. THIS IS NOT A BUG.
					//  The CBC would display this behavior, too.
					if (env.getFirstRobot() != null && !isPaused())
						for (int i = 0; i < analogs.length; i++)
							if (analogs[i].getValueType() != 0)
								// update sensors
								analogs[i].setValue(env.getFirstRobot().analog(i));
					lastUpdate = time;
				}
				if (time - lastMove >= 10L) {
					long dt = time - lastMove;
					// handle 10ms tasks (move robot)
					if (!isPaused())
						for (SimRobot bot : env.getRobots())
							bot.move(dt, bot.collide(dt));
					lastMove = time;
				}
				try {
					Thread.sleep(2L);
				} catch (Exception e) {
					return;
				}
			}
		}
	}

	/**
	 * Listens for interface events.
	 */
	private class EventListener extends MouseAdapter implements ActionListener,
			MouseMotionListener, KeyListener {
		// The robot which is being moved.
		private SimRobot dragging;
		// Whether bot is being spun.
		private boolean spin;

		public void mouseMoved(MouseEvent e) { }
		public void mouseDragged(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			if (dragging != null) {
				Location location = dragging.getLocation();
				double gx = gc.invTransform(x * RobotConstants.PIXELS_TO_MM);
				double gy = gc.invTransform(y * RobotConstants.PIXELS_TO_MM);
				if (spin)
					// Change rotation of object being moved
					location.setTheta(Math.atan2(location.getY() - gy, location.getX() - gx));
				else {
					// Change the location of object being moved
					location.setX(gx);
					location.setY(gy);
				}
				gc.repaint();
			}
		}
		public void mousePressed(MouseEvent e) {
			int x = Math.round(gc.invTransform(RobotConstants.PIXELS_TO_MM * e.getX())),
				y = Math.round(gc.invTransform(RobotConstants.PIXELS_TO_MM * e.getY()));
			dragging = null;
			if (god.isSelected()) {
				// look for a robot to pick up
				for (SimRobot r : env.getRobots())
					if (r.obj.hit(x, y)) {
						dragging = r;
						break;
					}
				if (dragging != null)
					spin = e.isControlDown() || e.getButton() != MouseEvent.BUTTON1;
			}
		}
		public void mouseReleased(MouseEvent e) {
			// drop the robot
			dragging = null;
			spin = false;
		}
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd == null) return;
			if (cmd.equals("load"))
				loadCode();
			else if (cmd.equals("play") && instance != null)
				play();
			else if (cmd.equals("god"))
				handOfGod();
			else if (cmd.equals("stop"))
				eStop();
			else if (cmd.equals("in"))
				zoom(1);
			else if (cmd.equals("out"))
				zoom(-1);
			else if (cmd.equals("choose"))
				// Check for A button
				buttons[0].setSelected(!buttons[0].isSelected());
			else if (cmd.equals("cancel"))
				// Check for B button
				buttons[1].setSelected(!buttons[1].isSelected());
			else if (cmd.equals("up"))
				// Check for UP button
				buttons[2].setSelected(!buttons[2].isSelected());
			else if (cmd.equals("down"))
				// Check for DOWN button
				buttons[3].setSelected(!buttons[3].isSelected());
			else if (cmd.equals("left"))
				// Check for LEFT button
				buttons[4].setSelected(!buttons[4].isSelected());
			else if (cmd.equals("right"))
				// Check for RIGHT button
				buttons[5].setSelected(!buttons[5].isSelected());
			else if (cmd.equals("black"))
				// Check for BLACK button
				buttons[6].setSelected(!buttons[6].isSelected());
			else if (cmd.equals("light"))
				// Light switch
				toggleLight();
			else if (cmd.equals("setup"))
				// Set up sensors
				setUpSensors();
			else if (cmd.startsWith("conf") && cmd.length() > 4) try {
				// Set up one sensor
				setUpSensor(Integer.parseInt(cmd.substring(4)));
			} catch (Exception ex) { }
		}
		public void keyPressed(KeyEvent e) {
			// Map key presses to buttons on the screen
			if (e.getKeyCode() == KeyEvent.VK_A)
				buttons[0].setSelected(true);
			else if (e.getKeyCode() == KeyEvent.VK_B)
				buttons[1].setSelected(true);
			else if (e.getKeyCode() == KeyEvent.VK_UP)
				buttons[2].setSelected(true);
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				buttons[3].setSelected(true);
			else if (e.getKeyCode() == KeyEvent.VK_LEFT)
				buttons[4].setSelected(true);
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				buttons[5].setSelected(true);
			else if (e.getKeyCode() == KeyEvent.VK_PERIOD)
				buttons[6].setSelected(true);
		}
		public void keyReleased(KeyEvent e) {
			// Map key presses to buttons on the screen
			if (e.getKeyCode() == KeyEvent.VK_A)
				buttons[0].setSelected(false);
			else if (e.getKeyCode() == KeyEvent.VK_B)
				buttons[1].setSelected(false);
			else if (e.getKeyCode() == KeyEvent.VK_UP)
				buttons[2].setSelected(false);
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				buttons[3].setSelected(false);
			else if (e.getKeyCode() == KeyEvent.VK_LEFT)
				buttons[4].setSelected(false);
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				buttons[5].setSelected(false);
			else if (e.getKeyCode() == KeyEvent.VK_PERIOD)
				buttons[6].setSelected(false);
			else if (e.getKeyCode() == KeyEvent.VK_SPACE)
				// play/pause
				play();
			else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				// kill!
				eStop();
			else if (e.getKeyCode() == KeyEvent.VK_PLUS ||
				e.getKeyCode() == KeyEvent.VK_EQUALS)
				// zoom in/out
				zoom(1);
			else if (e.getKeyCode() == KeyEvent.VK_MINUS ||
				e.getKeyCode() == KeyEvent.VK_UNDERSCORE)
				zoom(-1);
			else if (e.getKeyCode() == KeyEvent.VK_C)
				// collision rendering on (currently no off)
				gc.renderCollision(true);
			else if (e.getKeyCode() == KeyEvent.VK_L)
				// start light
				toggleLight();
			else if (e.getKeyCode() == KeyEvent.VK_S)
				// Set up sensors
				setUpSensors();
		}
		public void keyTyped(KeyEvent e) {}
	}

	/**
	 * A string writer which can be reset (LCD clear).
	 */
	protected static class ClearableStringWriter extends Writer {
		private StringBuffer buf;

		// Creates an empty buffer.
		public ClearableStringWriter() {
			super();
			clear();
		}
		// Clears the buffer.
		public void clear() {
			buf = new StringBuffer(4096);
		}
		public void close() throws IOException { }
		public void flush() throws IOException { }

		// Writes to the buffer.
		public void write(char[] cbuf, int off, int len) throws IOException {
			buf.append(cbuf, off, len);
		}
		public String toString() {
			return buf.toString();
		}
		public int length() {
			return buf.length();
		}
	}

	// Accepts IC and KISS-C files
	private static class IKCFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File f) {
			String name = f.getName();
			return f.isDirectory() || name.endsWith(".c") || name.endsWith(".ic") ||
				name.endsWith(".h");
		}

		public String getDescription() {
			return "IC and KISS-C files";
		}
	}
}