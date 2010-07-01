package org.icx.sim;

import java.util.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.URL;

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
	// All robots (currently only one).
	private LinkedList<SimRobot> robots;
	// All simulated items excluding robots.
	private List<SimObject> items;
	// The play/pause button.
	private JButton pp;
	// The Hand of God button.
	private JButton god;
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
	// The current program (currently only one)
	private BotballProgram instance;

	/**
	 * Creates a Simulator but does not start it.
	 */
	public Simulator() {
		super("Botball Simulator");
		setupUI();
		playIcon = getIcon("play");
		pauseIcon = getIcon("pause");
		pause = true;
		str = new ClearableStringWriter();
		lcdWriter = new PrintWriter(str);
		robots = new LinkedList<SimRobot>();
		items = new ArrayList<SimObject>(100);
		setPP();
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
	 * Refreshes the LCD screen.
	 */
	public synchronized void refreshLCD() {
		lcd.setText(str.toString());
		try {
			lcd.setCaretPosition(str.length());
		} catch (Exception e) {
			// TODO identify what causes this exception and fix it
			//  It is nonfatal and appears not harmful to execution.
			System.out.println("Refresh LCD: caret bug");
			e.printStackTrace(System.out);
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
		events = new EventListener();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener(events);
		// initialize
		motors = new MotorComponent[4];
		servos = new MotorComponent[4];
		gc = new GraphicsComponent();
		gc.addKeyListener(events);
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JComponent vert = new Box(BoxLayout.Y_AXIS);
		JPanel across = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
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
		// graphics component is scrollable
		gc.setPreferredSize(new Dimension(1024, 768));
		JScrollPane p = new JScrollPane(gc);
		p.setBorder(BorderFactory.createEmptyBorder());
		c.add(controls, BorderLayout.NORTH);
		c.add(p, BorderLayout.CENTER);
		vert = new Box(BoxLayout.Y_AXIS);
		// controls for buttons and digitals
		controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JLabel lbl = new JLabel("Buttons");
		lbl.setFont(lbl.getFont().deriveFont(16.0f));
		buttons = new JButton[] {
			new JButton("A"),
			new JButton("B"),
			new JButton("U"),
			new JButton("D"),
			new JButton("L"),
			new JButton("R"),
			new JButton("Black")
		};
		// intialize controller buttons
		controls.add(lbl);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setFocusable(false);
			buttons[i].setActionCommand(buttons[i].getText().toLowerCase());
			buttons[i].addActionListener(events);
			controls.add(buttons[i]);
		}
		vert.add(controls);
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
		c.add(vert, BorderLayout.SOUTH);
		vert = new JPanel(new VerticalFlow(false));
		lbl = new JLabel("Analog");
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
		analogs[8].setValueType(2);
		analogs[9] = new AnalogSlider("AY");
		analogs[9].setValueType(2);
		analogs[10] = new AnalogSlider("AZ");
		analogs[10].setValue(512 + 128);
		analogs[10].setValueType(2);
		vert.add(analogs[8]);
		vert.add(analogs[9]);
		vert.add(analogs[10]);
		// LCD screen
		lcd = new JTextArea(8, 30);
		lcd.setEditable(false);
		lcd.setFont(new Font("Arial", Font.PLAIN, 11));
		p = new JScrollPane(lcd);
		p.setBorder(BorderFactory.createEtchedBorder());
		vert.add(p);
		// final words
		c.add(vert, BorderLayout.EAST);
		setSize(1000, 800);
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
		setVisible(true);
		requestFocus();
		requestFocus();
		new GraphicsThread().start();
	}
	/**
	 * Returns whether the simulator is paused
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
		items.add(obj);
		gc.add(obj);
	}
	/**
	 * Deletes an object from the simulation (wall, static, etc.)
	 * 
	 * @param obj the object to remove
	 */
	public void remove(StaticObject obj) {
		items.remove(obj);
		gc.remove(obj);
	}
	/**
	 * Returns an XBC style button mask of controller buttons.
	 * 
	 * @return the button mask
	 */
	public int buttonMask() {
		int mask = 0;
		if (buttons[0].isSelected()) mask |= BotballProgram.A_BUTTON;
		if (buttons[1].isSelected()) mask |= BotballProgram.B_BUTTON;
		if (buttons[2].isSelected()) mask |= BotballProgram.UP_BUTTON;
		if (buttons[3].isSelected()) mask |= BotballProgram.DOWN_BUTTON;
		if (buttons[4].isSelected()) mask |= BotballProgram.LEFT_BUTTON;
		if (buttons[5].isSelected()) mask |= BotballProgram.RIGHT_BUTTON;
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
				JOptionPane.showMessageDialog(null, "Could not find a required image.",
					"Error", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
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
			JOptionPane.showMessageDialog(null, "Could not find a required image.",
				"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			return null;
		}
	}
	// Sets the play/pause status button appropriately
	protected void setPP() {
		if (pause)
			pp.setIcon(playIcon);
		else
			pp.setIcon(pauseIcon);
		// manage timing
		if (instance != null && instance._isRunning())
			if (pause)
				instance._stopTiming();
			else
				instance._startTiming();
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
		robots.add(r);
	}
	/**
	 * Removes a robot from the simulation.
	 * 
	 * @param r the robot to remove
	 */
	public void removeRobot(SimRobot r) {
		gc.remove(r);
		robots.remove(r);
	}
	// Changes the tint of all robots
	protected void tintAllRobots(Color color) {
		for (SimRobot r : robots)
			r.setTint(color);
		gc.repaint();
	}
	// Loads code into the simulator
	private void loadCode() {
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
		pause = true;
		setPP();
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
		pause = true;
		setPP();
		// kill motors/servos
		disableServos();
		ao();
		// TODO stop robot, clear create status...
	}
	/**
	 * Runs or pauses the program.
	 */
	public void play() {
		pause = !pause;
		if (!instance._isRunning() && !pause) {
			clearLCD();
			// run user code
			Thread pRun = new Thread(Simulator.this);
			pRun.setPriority(Thread.MAX_PRIORITY - 1);
			pRun.setName("User Code Run");
			pRun.start();
		}
		// update status
		setPP();
	}
	/**
	 * Activates or deactives the Hand of God button.
	 */
	public void handOfGod() {
		if (god.isSelected()) {
			pause = wasPaused;
			setPP();
			// turn off hand of god
			pp.setEnabled(true);
			tintAllRobots(null);
			gc.renderHandles(true);
			god.setSelected(false);
			god.setText("Hand of God");
		} else {
			wasPaused = pause;
			pause = true;
			// turn on hand of god
			setPP();
			pp.setEnabled(false);
			tintAllRobots(Color.YELLOW);
			gc.renderHandles(false);
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
			CodeParser.syntax(r);
			r.close();
			// compile user code
			int res = com.sun.tools.javac.Main.compile(new String[] { "Program.java" },
				getLCDWriter());
			if (res == 0) {
				print("Compile succeeded.\n");
				// load into memory
				ICClassLoader ic = new ICClassLoader();
				Class<?> program = ic.loadClass("Program");
				instance = (BotballProgram)program.newInstance();
				instance._setSim(this, robots.getFirst());
			} else {
				print("Compile failed!\n");
				instance = null;
			}
			pp.setEnabled(true);
			new File("Program.class").delete();
		} catch (Exception e) {
			// oh no!
			print("Compile failed.\n");
			instance = null;
			pp.setEnabled(true);
		} else
			instance.invokeMain();
	}

	/**
	 * Class which refreshes the screen.
	 */
	private class GraphicsThread extends Thread {
		public GraphicsThread() {
			super("Graphics Thread");
		}
		public void run() {
			while (true) {
				gc.repaint();
				// change icon when program ends
				if (instance != null && !instance._isRunning() && !pause) {
					pause = true;
					setPP();
				}
				try {
					Thread.sleep(30L);
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

		public void mouseMoved(MouseEvent e) { }
		public void mouseDragged(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			if (dragging != null) {
				// Change the location of object being moved
				Location location = dragging.getLocation();
				location.setX(x);
				location.setY(y);
				gc.repaint();
			}
		}
		public void mousePressed(MouseEvent e) {
			int x = e.getX(), y = e.getY();
			dragging = null;
			if (god.isSelected()) {
				// look for a robot to pick up
				for (SimRobot r : robots)
					if (r.obj.hit(x, y)) {
						dragging = r;
						break;
					}
			}
		}
		public void mouseReleased(MouseEvent e) {
			// drop the robot
			dragging = null;
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
			else if (cmd.equals("a"))
				// Check for A button
				buttons[0].setSelected(!buttons[0].isSelected());
			else if (cmd.equals("b"))
				// Check for B button
				buttons[1].setSelected(!buttons[1].isSelected());
			else if (cmd.equals("u"))
				// Check for UP button
				buttons[2].setSelected(!buttons[2].isSelected());
			else if (cmd.equals("d"))
				// Check for DOWN button
				buttons[3].setSelected(!buttons[3].isSelected());
			else if (cmd.equals("l"))
				// Check for LEFT button
				buttons[4].setSelected(!buttons[4].isSelected());
			else if (cmd.equals("r"))
				// Check for RIGHT button
				buttons[5].setSelected(!buttons[5].isSelected());
			else if (cmd.equals("black"))
				// Check for BLACK button
				buttons[6].setSelected(!buttons[6].isSelected());
		}
		public void keyPressed(KeyEvent e) {
			// Map key presses to buttons on the screen
			if (e.getKeyCode() == KeyEvent.VK_A)
				buttons[0].setSelected(true);
			if (e.getKeyCode() == KeyEvent.VK_B)
				buttons[1].setSelected(true);
			if (e.getKeyCode() == KeyEvent.VK_UP)
				buttons[2].setSelected(true);
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				buttons[3].setSelected(true);
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				buttons[4].setSelected(true);
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				buttons[5].setSelected(true);
			if (e.getKeyCode() == KeyEvent.VK_PERIOD)
				buttons[6].setSelected(true);
		}
		public void keyReleased(KeyEvent e) {
			// Map key presses to buttons on the screen
			if (e.getKeyCode() == KeyEvent.VK_A)
				buttons[0].setSelected(false);
			if (e.getKeyCode() == KeyEvent.VK_B)
				buttons[1].setSelected(false);
			if (e.getKeyCode() == KeyEvent.VK_UP)
				buttons[2].setSelected(false);
			if (e.getKeyCode() == KeyEvent.VK_DOWN)
				buttons[3].setSelected(false);
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				buttons[4].setSelected(false);
			if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				buttons[5].setSelected(false);
			if (e.getKeyCode() == KeyEvent.VK_PERIOD)
				buttons[6].setSelected(false);
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