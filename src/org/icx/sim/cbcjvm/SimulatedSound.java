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

import cbccore.low.Sound;
import org.icx.sim.BotballProgram;

/**
 * Contains stubs for botballsim.
 * Documentation stolen from the KISS-C documentation
 *
 * @author  Benjamin Woodruff, Braden McDorman
 * @see     cbccore.low.Sound
 */

public class SimulatedSound extends Sound {
	
	protected BotballProgram bp;
	
	public SimulatedSound(BotballProgram bp) { this.bp = bp; }
	
	public void tone(int frequency, int duration) {
		bp.tone(frequency, duration);
	}
	
	public void beep() { bp.beep() }
}
