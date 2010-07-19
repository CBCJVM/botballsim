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

/**
 * Tests the simulator.
 * 
 * @author Stephen Carlson
 */
public class SimTest {
	// Tests the simulator with the board in board.txt.
	public static void main(String[] args) {
		// really?
		if (GraphicsEnvironment.isHeadless()) {
			System.err.println("This is a graphical simulator which requires a display.");
			System.err.println(" If you are trying to run it on a terminal client, make");
			System.err.println(" sure that X11 forwarding is enabled and the DISPLAY");
			System.err.println(" environment variable is set.");
			System.exit(1);
		}
		Simulator s = new Simulator();
		// read board from board.txt
		BoardReader.loadBoard(s, "board.txt");
		s.start();
		// allow choice of bots from robots.txt
		String type = BoardReader.selectRobot(s);
		if (type == null) System.exit(0);
		SimRobot r = new SimRobot(s, type);
		// configure start at 50 cm from the corner
		//  (no support for load/save configuration until it works)
		r.getSetup().setStart(new Location(600, 600));
		s.addRobot(r);
	}
}