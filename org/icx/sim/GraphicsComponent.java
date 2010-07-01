package org.icx.sim;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * A class which represents the simulation area.
 * 
 * @author Stephen Carlson
 */
public class GraphicsComponent extends JComponent {
	private static final long serialVersionUID = 0L;

	// Background color for normal execution.
	private static final Color BG = new Color(224, 224, 224);
	// Background color when beeping.
	private static final Color BEEP_BG = new Color(224, 112, 112);

	// The list of displayable objects.
	private java.util.List<DisplayObject> display;
	// Current background color.
	private Color bg;
	// Whether all movable objects get rotation handles.
	private boolean handles;

	/**
	 * Creates a new simulation area.
	 */
	public GraphicsComponent() {
		super();
		handles = false;
		bg = BG;
		display = new LinkedList<DisplayObject>();
	}

	/**
	 * Gets the list of displayed objects.
	 * 
	 * @return the list of graphical items
	 */
	public java.util.List<DisplayObject> getDisplay() {
		return display;
	}

	/**
	 * Sets background color to beeper on.
	 */
	public void setBeeperOn() {
		bg = BEEP_BG;
		repaint();
	}

	/**
	 * Sets background color to beeper off.
	 */
	public void setBeeperOff() {
		bg = BG;
		repaint();
	}

	/**
	 * Turns drag/rotation handles on and off.
	 * 
	 * @param render whether handles should be shown
	 */
	public void renderHandles(boolean render) {
		handles = render;
		repaint();
	}

	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		g.setColor(bg);
		g.fillRect(0, 0, getWidth(), getHeight());
		// render each object with antialias on
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (DisplayObject o : display)
			o.draw(this, g);
	}

	public void update(Graphics g1) {
		paint(g1);
	}

	/**
	 * Adds the graphical object to be rendered.
	 * 
	 * @param o the object to be added
	 */
	public void add(DisplayObject o) {
		display.add(o);
	}

	/**
	 * Removes the graphical object from the area.
	 * 
	 * @param o the object to remove
	 */
	public void remove(DisplayObject o) {
		display.remove(o);
	}

	/**
	 * Removes the simulation object from the area.
	 * 
	 * @param o the object to be removed
	 */
	public void remove(SimObject o) {
		display.remove(o.obj);
	}

	/**
	 * Adds the simulation object to be rendered.
	 * 
	 * @param o the object to be added
	 */
	public void add(SimObject o) {
		display.add(o.obj);
	}

	/**
	 * Removes all simulation objects.
	 */
	public void clear() {
		display.clear();
	}

	/**
	 * Returns the number of graphical objects.
	 * 
	 * @return the number of displayed items
	 */
	public int count() {
		return display.size();
	}
}