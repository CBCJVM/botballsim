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

/**
 * Tests the simulator.
 * 
 * @author Stephen Carlson
 */
public class SimTest {
	// Tests the simulator with the board in board.txt.
	public static void main(String[] args) {
		Simulator s = new Simulator();
		BoardReader.loadBoard(s, "board.txt");
		s.start();
		// for now, support cbc and create
		String type = BoardReader.selectRobot(s);
		if (type == null) System.exit(0);
		SimRobot r = new SimRobot(s, type);
		// drop the robot at 50 cm from the corner
		r.setLocation(new Location(600, 600));
		s.addRobot(r);
	}
}