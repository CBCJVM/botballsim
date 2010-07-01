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
		// for now, support cbc and create
		BoardReader.loadBoard(s, "board.txt");
		s.start();
		String type = BoardReader.selectRobot(s);
		if (type == null) System.exit(0);
		SimRobot r = new SimRobot(s, type);
		r.setLocation(new Location(300, 200));
		s.addRobot(r);
	}
}