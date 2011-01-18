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

import cbccore.low.*;
import org.icx.sim.BotballProgram;

/**
 * Links BotballProgram to CBCJVM's simulator API. Idealy botballsim should have
 * a more extensible way of plugging CBCJVM into it, but I'm too lazy, so I'm
 * hacking all the function calls on top of BotballProgram.
 *
 * @author Benjamin Woodruff
 *
 */

public class JVMSim extends cbccore.low.Simulator {
	
	BotballProgram bp = new BotballProgram();
	
	public JVMSim() {
		super();
		init(new SimulatedSound(bp), new SimulatedSensor(bp),
		     new SimulatedDevice(bp), new SimulatedDisplay(bp),
		     new SimulatedInput(bp), new SimulatedServo(bp),
		     new SimulatedMotor(bp), new SimulatedCamera(bp),
		     new SimulatedCreate(bp));
	}
	
	public BotballProgram getBotballProgram() {
		return bp;
	}
}
