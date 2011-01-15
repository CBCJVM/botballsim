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

import cbccore.low.Display;
import org.icx.sim.BotballProgram;

/**
 * Contains stubs for botballsim.
 * Documentation stolen from the KISS-C documentation
 *
 * @author  Benjamin Woodruff, Braden McDorman
 * @see     cbccore.low.Display
 */

public class SimulatedDisplay extends Display {
	
	protected BotballProgram bp;
	
	public SimulatedDisplay(BotballProgram bp) { this.bp = bp; }
	
	/**
	 * Clears display and pust cursor in upper left
	 */
	public void display_clear() { bp.display_clear(); }
	
	
	
	/**
	 * Clear the CBC display
	 */
	public native void cbc_display_clear() { bp.cbc_display_clear(); }
}
