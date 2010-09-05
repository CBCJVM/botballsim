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

import java.util.*;
import java.io.*;
import javax.swing.*;

/**
 * A class which loads the board from a file into the simulator.
 * 
 * See the example board file provided for the syntax.
 */
public class BoardReader {
	// Loads board into the simulator from the given file
	//  Note that board.txt is EXTERNAL for easy modding.
	public static void loadBoard(Simulator sim, String file) {
		try {
			loadBoard(sim, new FileInputStream(file));
		} catch (IOException e) {
			Simulator.die("Game board must be specified in a file named \"" + file + "\".");
		}
	}
	// Loads board into the simulator from the given stream
	public static void loadBoard(Simulator sim, InputStream is) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line, type, dir; StringTokenizer str;
			int len, finalDir; float x, y; Wall wall;
			while ((line = br.readLine()) != null) {
				line = line.trim().toLowerCase();
				// ignore comment or blank
				if (line.length() < 1 || line.charAt(0) == '#') continue;
				str = new StringTokenizer(line, ",");
				if (str.countTokens() < 5) continue;
				// read info
				type = str.nextToken().trim();
				dir = str.nextToken().trim();
				len = Integer.parseInt(str.nextToken().trim());
				x = Integer.parseInt(str.nextToken().trim());
				y = Integer.parseInt(str.nextToken().trim());
				finalDir = getDirection(dir);
				if (type.equals("pvc") || type.equals("wall") || type.equals("")) {
					// wall, direction needs to be changed
					if (dir.equals("ttb") || dir.startsWith("vert"))
						finalDir = Wall.DIR_TTB;
					else if (dir.equals("ltr") || dir.startsWith("horiz"))
						finalDir = Wall.DIR_LTR;
					else
						// error here?
						finalDir = Wall.DIR_LTR;
					wall = new Wall(Wall.TYPE_PVC, finalDir,
						Math.round(len * RobotConstants.MM_TO_PIXELS));
					wall.setLocation(new Location(x, y));
					sim.add(wall);
				} else if (type.equals("corner") || type.equals("right") || type.equals("2way")) {
					// right angle
					wall = new Wall(Wall.TYPE_PVC_CORNER, finalDir, -1);
					wall.setLocation(new Location(x, y));
					sim.add(wall);
				} else if (type.equals("t") || type.equals("tee") || type.equals("3way")) {
					// T (3 way)
					wall = new Wall(Wall.TYPE_PVC_T, finalDir, -1);
					wall.setLocation(new Location(x, y));
					sim.add(wall);
				} else if (type.equals("inter") || type.equals("cross") || type.equals("4way")) {
					// intersection
					wall = new Wall(Wall.TYPE_PVC_INTERSECTION, finalDir, -1);
					wall.setLocation(new Location(x, y));
					sim.add(wall);
				}
			}
			br.close();
		} catch (Exception e) {
			Simulator.die("Failed to read game board.");
		}
	}
	// Interprets a direction into an integer for compass oriented objects
	public static int getDirection(String dir) {
		// allow U/D/L/R and N/S/E/W
		//  for the directionally challenged among us
		if (dir.equals("up") || dir.equals("north"))
			return Wall.POINT_NORTH;
		if (dir.equals("right") || dir.equals("east"))
			return Wall.POINT_EAST;
		if (dir.equals("down") || dir.equals("south"))
			return Wall.POINT_SOUTH;
		if (dir.equals("left") || dir.equals("west"))
			return Wall.POINT_WEST;
		return Wall.POINT_NORTH;
	}
	// Shows a robot selection list.
	public static String selectRobot(Simulator parent) {
		String[] robots = RobotsFile.getEnabled();
		if (robots == null || robots.length < 1)
			Simulator.die("No robots are enabled. Please enable at least one type in robots.txt.");
		// if parent is null, unparented
		Object bot = JOptionPane.showInputDialog(parent, "Select a robot type:", "Select Robot",
			JOptionPane.QUESTION_MESSAGE, null, robots, robots[0]);
		if (bot == null) return null;
		return (String)bot;
	}
}