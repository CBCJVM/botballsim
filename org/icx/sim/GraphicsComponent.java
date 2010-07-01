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
import java.util.*;
import javax.swing.*;

/**
 * A class which represents the simulation area.
 * 
 * @author Stephen Carlson
 */
public class GraphicsComponent extends JComponent {
	private static final long serialVersionUID = 0L;

	// Background color for disallowed area.
	private static final Color BORDER = new Color(240, 240, 240);
	// Background color for normal execution.
	private static final Color BG = Color.WHITE;
	// Background color when beeping.
	private static final Color BEEP_BG = new Color(224, 112, 112);

	// The list of displayable objects.
	private java.util.List<DisplayObject> display;
	// Current background color.
	private Color bg;
	// Whether all movable objects get rotation handles.
	private boolean handles;
	// The size before any zoom is applied.
	private Dimension realSize;
	// If zoom is >= 1, then sizes are multiplied by (zoom + 1)
	//  zoom = 0, 1:1
	//  zoom <= 1, sizes divided by (-zoom + 1)
	private int zoom;

	/**
	 * Creates a new simulation area with the given size in mm.
	 * 
	 * @param width the width
	 * @param height the height
	 */
	public GraphicsComponent(int width, int height) {
		super();
		realSize = new Dimension(Math.round(width * RobotConstants.MM_TO_PIXELS),
			Math.round(height * RobotConstants.MM_TO_PIXELS));
		zoom = 1;
		handles = false;
		bg = BG;
		display = new LinkedList<DisplayObject>();
		resize();
	}

	/**
	 * Gets the current zoom.
	 * 
	 * @return the zoom level
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * Changes the zoom.
	 * 
	 * @param newZoom the new zoom level
	 */
	public void setZoom(int newZoom) {
		zoom = newZoom;
		resize();
		repaint();
	}

	/**
	 * Resizes the component to match the zoom.
	 */
	private void resize() {
		Dimension toZoom;
		if (zoom >= 1)
			// zoom in
			toZoom = new Dimension(realSize.width * (zoom + 1), realSize.height * (zoom + 1));
		else if (zoom <= -1)
			// zoom out
			toZoom = new Dimension(realSize.width / (1 - zoom), realSize.height / (1 - zoom));
		else
			toZoom = realSize;
		// resize component
		setSize(toZoom);
		setPreferredSize(toZoom);
		setMinimumSize(toZoom);
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
		Dimension ps = getPreferredSize();
		g.setColor(BORDER);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(bg);
		g.fillRect(0, 0, ps.width, ps.height);
		Graphics2D g2 = (Graphics2D)g.create();
		if (zoom >= 1)
			g2.scale(zoom + 1, zoom + 1);
		else if (zoom <= -1)
			g2.scale(1.0 / (1.0 - zoom), 1.0 / (1.0 - zoom));
		// render each object with antialias on and good smooth rotation
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		synchronized (display) {
			// we have the lock, so render list
			for (DisplayObject o : display)
				o.draw(this, g2);
		}
		g2.dispose();
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
		synchronized (display) {
			display.add(o);
		}
	}

	/**
	 * Removes the graphical object from the area.
	 * 
	 * @param o the object to remove
	 */
	public void remove(DisplayObject o) {
		synchronized (display) {
			display.remove(o);
		}
	}

	/**
	 * Removes the simulation object from the area.
	 * 
	 * @param o the object to be removed
	 */
	public void remove(SimObject o) {
		synchronized (display) {
			display.remove(o.obj);
		}
	}

	/**
	 * Adds the simulation object to be rendered.
	 * 
	 * @param o the object to be added
	 */
	public void add(SimObject o) {
		synchronized (display) {
			display.add(o.obj);
		}
	}

	/**
	 * Removes all simulation objects.
	 */
	public void clear() {
		synchronized (display) {
			display.clear();
		}
	}

	/**
	 * Returns the number of graphical objects.
	 * 
	 * @return the number of displayed items
	 */
	public int count() {
		synchronized (display) {
			return display.size();
		}
	}
}