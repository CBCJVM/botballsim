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

import cbccore.low.Input;
import org.icx.sim.BotballProgram;

/**
 * Contains stubs for botballsim.
 * Documentation stolen from the KISS-C documentation
 *
 * @author  Benjamin Woodruff, Braden McDorman
 * @see     cbccore.low.Input
 */

public class SimulatedInput extends Input {
	
	protected BotballProgram bp;
	
	public SimulatedInput(BotballProgram bp) { this.bp = bp; }
	
	/**
	 * Is the up button pressed?
	 * 
	 * @return  1 for yes, 0 for no
	 */
	public native int up_button();
	
	
	/**
	 * Is the down button pressed?
	 * 
	 * @return  1 for yes, 0 for no
	 */
	public native int down_button();
	
	
	
	/**
	 * Is the left button pressed?
	 * 
	 * @return  1 for yes, 0 for no
	 */
	public native int left_button();
	
	
	
	/**
	 * Is the right button pressed?
	 * 
	 * @return  1 for yes, 0 for no
	 */
	public native int right_button();
	
	
	
	/**
	 * Is the a button pressed?
	 * 
	 * @return  1 for yes, 0 for no
	 */
	public native int a_button();
	
	
	
	/**
	 * Is the b button pressed?
	 * 
	 * @return  1 for yes, 0 for no
	 */
	public native int b_button();
	
	
	
	/**
	 * Is the black hardware button pressed? (simulator "bl")
	 * 
	 * @return  1 for yes, 0 for no
	 */
	public native int black_button();
}
