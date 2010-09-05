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

/**
 * A class which reads in all of the robot properties from robots.txt.
 * This happens only once, so changing the file will only take effect
 *  after restarting the simulator.
 */
public class RobotsFile {
	// Where actual robot data is stored.
	private static Properties data;

	/**
	 * Gets the specified parameter as an integer.
	 * 
	 * @param name the parameter name
	 * @return its value as an int
	 */
	public static int getParameterInt(String name) {
		try {
			return Integer.parseInt(getParameter(name));
		} catch (NumberFormatException e) {
			Simulator.die("In robots.txt, parameter " + name + " must be a number.");
			return 0;
		}
	}
	/**
	 * Gets the specified parameter as a long.
	 * 
	 * @param name the parameter name
	 * @return its value as a long
	 */
	public static long getParameterLong(String name) {
		try {
			return Long.parseLong(getParameter(name));
		} catch (NumberFormatException e) {
			Simulator.die("In robots.txt, parameter " + name + " must be a number.");
			return 0l;
		}
	}
	/**
	 * Gets the specified parameter as a float.
	 * 
	 * @param name the parameter name
	 * @return its value as a float
	 */
	public static float getParameterFloat(String name) {
		try {
			return Float.parseFloat(getParameter(name));
		} catch (NumberFormatException e) {
			Simulator.die("In robots.txt, parameter " + name + " must be a number.");
			return 0.f;
		}
	}
	/**
	 * Gets the specified parameter as a double.
	 * 
	 * @param name the parameter name
	 * @return its value as a double
	 */
	public static double getParameterDouble(String name) {
		try {
			return Double.parseDouble(getParameter(name));
		} catch (NumberFormatException e) {
			Simulator.die("In robots.txt, parameter " + name + " must be a number.");
			return 0.;
		}
	}
	/**
	 * Gets the specified parameter as a string.
	 * 
	 * @param name the parameter name
	 * @return its value as a string
	 */
	public static String getParameter(String name) {
		if (data == null) readFile();
		String prop = data.getProperty(name, "").trim();
		if (prop.length() < 1)
			Simulator.die("In robots.txt, parameter " + name + " must be defined.");
		return prop;
	}
	/**
	 * Gets the specified parameter as a string, with the specified default.
	 * 
	 * @param name the parameter name
	 * @param def the default value
	 * @return its value as a string
	 */
	public static String getParameter(String name, String def) {
		if (data == null) readFile();
		String prop = data.getProperty(name, def).trim();
		if (prop.length() < 1) return def;
		return prop;
	}
	/**
	 * Fetches an array of enabled robot designs.
	 * 
	 * @return the available robots
	 */
	public static String[] getEnabled() {
		StringTokenizer bots = new StringTokenizer(getParameter("enable"), ",");
		String[] ret = new String[bots.countTokens()];
		for (int i = 0; i < ret.length && bots.hasMoreTokens(); i++)
			ret[i] = bots.nextToken().trim();
		return ret;
	}
	// Reads in the robot data.
	private static void readFile() {
		try {
			data = new Properties();
			java.io.InputStream is = new java.io.FileInputStream("robots.txt");
			data.load(is);
			is.close();
		} catch (Exception e) {
			Simulator.die("Could not read information from \"robots.txt\".");
		}
	}

	private RobotsFile() { }
}