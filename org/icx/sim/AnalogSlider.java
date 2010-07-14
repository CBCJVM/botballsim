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
import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * A class that represents a slider used for analog input.
 * 
 * @author Stephen Carlson
 */
public class AnalogSlider extends JPanel {
	private static final long serialVersionUID = 0L;

	// Actual user input slider.
	private TheSlider slide;
	// Types of input.
	private JComboBox types;
	// Current value.
	private JLabel label;

	/**
	 * Creates a new analog slider with the given input name.
	 * 
	 * @param name the input name
	 */
	public AnalogSlider(String name) {
		super(new FlowLayout(FlowLayout.CENTER, 2, 0));
		// user input slider
		slide = new TheSlider();
		add(new JLabel(name));
		add(slide);
		slide.setFont(slide.getFont().deriveFont(9.f));
		JPanel vert = new JPanel(new VerticalFlow(false));
		// available types:
		//  Set: sets the value to constant
		types = new JComboBox();
		types.addItem("Set");
		//  Sets value to random quantity within a given range
		types.addItem("Random");
		//  Sets value to real-world sensor
		types.addItem("Real...");
		types.setFocusable(false);
		// Label for the value
		label = new JLabel("1023");
		label.setFont(new Font("Monospaced", Font.PLAIN, 10));
		vert.add(Box.createVerticalStrut(1));
		vert.add(label);
		vert.add(Box.createVerticalStrut(2));
		vert.add(types);
		vert.add(Box.createVerticalStrut(1));
		add(vert);
		// divide sensors visually on screen
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
	}
	/**
	 * Gets the value type for this sensor.
	 * 
	 * @return the type of value to be used
	 */
	public int getValueType() {
		return types.getSelectedIndex();
	}
	/**
	 * Changes the value type for this sensor.
	 *  Actual logic for random and real is handled by Simulator
	 * 
	 * @param type the new value type:
	 * 0 - Set
	 * 1 - Random
	 * 2 - Real
	 */
	public void setValueType(int type) {
		types.setSelectedIndex(type);
	}
	/**
	 * Disables the type selection drop down while running.
	 */
	public void disableType() {
		types.setEnabled(false);
	}
	/**
	 * Enables the type selection drop down while paused.
	 */
	public void enableType() {
		types.setEnabled(true);
	}
	/**
	 * Gets the value of the slider.
	 * 
	 * @return the slider value
	 */
	public int getValue() {
		return slide.getValue();
	}
	/**
	 * Changes the value of the slider.
	 *  Use if sensor is externally controlled to update values on screen.
	 * 
	 * @param newValue the new sensor value
	 */
	public void setValue(int newValue) {
		slide.setValue(newValue);
	}

	/**
	 * A brief slider extension which sets UI preferences and handles a special case.
	 */
	protected class TheSlider extends JSlider {
		private static final long serialVersionUID = 0L;

		/**
		 * Creates a new slider.
		 */
		public TheSlider() {
			super(SwingConstants.HORIZONTAL, 0, 1024, 1023);
			setUI(new SliderUI(this));
			setFocusable(false);
			setPaintLabels(true);
			setPaintTicks(true);
			setMajorTickSpacing(256);
			setMinorTickSpacing(64);
		}
		public void setValue(int n) {
			// if initialized from 0...1023, no label at end
			//  need to suppress 1024 which can't be returned normally
			if (n >= 1024) n = 1023;
			if (n < 0) n = 0;
			label.setText(Integer.toString(n));
			super.setValue(n);
		}
	}

	/**
	 * An interface for the analog slider to show a red tab and gray track.
	 */
	private class SliderUI extends BasicSliderUI {
		public SliderUI(JSlider b) {
			super(b);
		}
		protected Dimension getThumbSize() {
			Dimension size = new Dimension();
			// set tab size based on direction
			if (slider.getOrientation() == JSlider.VERTICAL) {
				size.width = 10;
				size.height = 4;
			} else {
				size.width = 4;
				size.height = 10;
			}
			return size;
		}
		public void paintThumb(Graphics g) {
			g.setColor(Color.RED);
			g.fillRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
		}
		public void paintFocus(Graphics g) { }
		public void paintTrack(Graphics g) {
			g.setColor(Color.GRAY);
			g.fillRect(trackRect.x, thumbRect.y, trackRect.width, thumbRect.height);
		}
	}
}