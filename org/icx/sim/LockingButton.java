package org.icx.sim;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.event.*;

/**
 * A button which will lock down if desired (NC/NO)
 * 
 * @author Stephen Carlson
 */
public class LockingButton extends JButton {
	private static final long serialVersionUID = 0L;

	// Icons to be used.
	// Icon management needs to be done eventually.
	private static final Icon no = Simulator.getIcon("pb-no");
	private static final Icon nc = Simulator.getIcon("pb-nc");
	private static final Icon nop = Simulator.getIcon("pb-nop");
	private static final Icon ncp = Simulator.getIcon("pb-ncp");

	// Whether button is locked down.
	private boolean lockDown;
	// Whether button is pressed.
	private boolean inv;

	/**
	 * Creates a button with the given title.
	 * 
	 * @param title the button title on the left
	 */
	public LockingButton(String title) {
		super(title, no);
		setHorizontalTextPosition(SwingConstants.LEFT);
		setUI(new BasicButtonUI());
		setFocusable(false);
		setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
		setContentAreaFilled(false);
		lockDown = inv = false;
		u();
		addMouseListener(new LocalMouseListener());
	}
	/**
	 * Updates the icon to match the attributes
	 */
	protected void u() {
		if (lockDown) {
			if (inv)
				setIcon(ncp);
			else
				setIcon(nc);
		} else {
			if (inv)
				setIcon(nop);
			else
				setIcon(no);
		}
	}
	/**
	 * Gets whether the button is locked down.
	 * 
	 * @return whether button is Normally Closed
	 */
	public boolean isLocked() {
		return lockDown;
	}
	/**
	 * Changes locked state (NC/NO).
	 * 
	 * @param lockDown whether the button should be locked down
	 */
	public void setLockState(boolean lockDown) {
		this.lockDown = lockDown;
		u();
	}
	public boolean isSelected() {
		return lockDown != inv;
	}
	public void setSelected(boolean status) {
		inv = status;
		u();
	}

	/**
	 * Handles button presses manually to allow right-click to swap.
	 */
	private class LocalMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			// shift and ctrl are for mac users :(
			if (e.getButton() == MouseEvent.BUTTON1 &&
					(e.getModifiers() & KeyEvent.SHIFT_MASK) == 0 &&
					(e.getModifiers() & KeyEvent.CTRL_MASK) == 0) {
				inv = true;
				u();
			}
		}
		public void mouseReleased(MouseEvent e) {
			int b = e.getButton();
			if (b != MouseEvent.BUTTON1 || (e.getModifiers() & KeyEvent.SHIFT_MASK) > 0
					|| (e.getModifiers() & KeyEvent.CTRL_MASK) > 0) {
				lockDown = !lockDown;
				u();
			} else {
				inv = false;
				u();
			}
		}
	}
}