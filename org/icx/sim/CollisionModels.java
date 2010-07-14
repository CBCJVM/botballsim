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
import java.awt.geom.*;

/**
 * A class which acts as a storage location for built-in collision models.
 * Also can read in a model from a file.
 *  Models are indexed via the robots.txt file into the array of available models.
 */
public class CollisionModels {
	// All models are shown here.
	private static Area[] models;

	// Makes all built-in models.
	//  TODO remove this method once files are properly implemented
	private static void buildModels() {
		// 4 Models: Create and generic CBC/XBC robot, Create left & right bumpers
		models = new Area[4];
		Area create = new Area(new Ellipse2D.Float(-165.f, -165.f, 330.f, 330.f));
		create.subtract(new Area(new Rectangle2D.Float(-165.f, -165.f, 40.f, 330.f)));
		models[0] = create;
		Area bc = new Area(new Rectangle2D.Float(-97.565f, -51.565f, 149.13f, 103.13f));
		bc.add(new Area(new Rectangle2D.Float(-36.f, -98.f, 72.f, 196.f)));
		models[1] = bc;
	}

	/**
	 * Gets the specified model. Units are indexed in mm. 0,0 is robot center
	 *  as defined by the picture, so that a rotating robot will
	 *  rotate the model in lock step.
	 * 
	 * @param index the model index to retrieve
	 * @return the specified model
	 */
	public static Area getModel(int index) {
		/*if (models == null) buildModels();
		if (index < 0 || index >= models.length)
			throw new IndexOutOfBoundsException(index + " is not a valid model");
		return models[index];*/
		return fromFile(Integer.toString(index));
	}

	/**
	 * Reads in a collision model from a file.
	 * 
	 * @param fileName the file to read, without path or extension
	 * @return the model from that file
	 */
	public static Area fromFile(String fileName) {
		Properties props = new Properties();
		try {
			FileInputStream fis = new FileInputStream("models/" + fileName + ".txt");
			props.load(fis);
			fis.close();
			return constructModel(props, "base");
		} catch (Exception e) {
			Simulator.die("Error reading collision model " + fileName + ".");
		} catch (Error e) {
			// memory or stack error from infinite recursion in referencing
			Simulator.die("Improperly designed collision model " + fileName + ".");
		}
		return null;
	}
	/**
	 * Constructs (recursively) the model with the specified name.
	 * 
	 * @param props the properties with model data
	 * @param name the model to construct
	 * @return that model's data, or an empty area if such a model is nonexistent
	 */
	private static Area constructModel(Properties props, String name) {
		String first = props.getProperty(name, "empty"), prop;
		Area ret = construct(props, first);
		// fetch sub properties
		for (int i = 0; (prop = props.getProperty(name + "." + i, "").trim())
				.length() > 0; i++) {
			// lines must have a , for the operation
			int index = prop.indexOf(',');
			if (index < 0) continue;
			String op = prop.substring(0, index).trim(),
				data = prop.substring(index + 1, prop.length()).trim();
			// operations here
			if (op.equals("add"))
				ret.add(construct(props, data));
			else if (op.equals("subtract"))
				ret.subtract(construct(props, data));
			else if (op.equals("intersect"))
				ret.intersect(construct(props, data));
			else if (op.equals("xor"))
				ret.exclusiveOr(construct(props, data));
			// else ignore it
		}
		return ret;
	}
	/**
	 * Constructs an area from the given prim or ref line.
	 * 
	 * @param props the properties with other information
	 * @param data the data information line, described in a built-in model file,
	 *  but without operation
	 * @return the data from that line
	 */
	private static Area construct(Properties props, String data) {
		StringTokenizer str = new StringTokenizer(data, ",");
		// should never happen
		if (str.countTokens() < 1) return new Area();
		String type = str.nextToken().trim();
		if (type.equals("full"))
			// as full as it gets
			return new Area(new Rectangle2D.Float(-Float.MAX_VALUE / 2, -Float.MAX_VALUE / 2,
				Float.MAX_VALUE - 1, Float.MAX_VALUE - 1));
		else if (type.equals("rect") && str.countTokens() > 3)
			// rectangle primitive
			return new Area(new Rectangle2D.Float(nextFloat(str), nextFloat(str),
				nextFloat(str), nextFloat(str)));
		else if (type.equals("ellipse") && str.countTokens() > 3)
			// ellipse primitive
			return new Area(new Ellipse2D.Float(nextFloat(str), nextFloat(str),
				nextFloat(str), nextFloat(str)));
		else if (type.equals("poly") && str.countTokens() % 2 == 0 && str.countTokens() > 2) {
			// polygon primitive
			Path2D path = new Path2D.Float();
			// read in lots of x, y pairs
			path.moveTo(nextFloat(str), nextFloat(str));
			while (str.countTokens() > 0)
				// iterate over points
				path.lineTo(nextFloat(str), nextFloat(str));
			path.closePath();
			return new Area(path);
		} else if (type.equals("empty"))
			return new Area();
		else if (props.getProperty(type, "").trim().length() > 0)
			// Slightly inefficient as it could lead to lots of repetitive constructions.
			//  However, smart models will avoid this, and this method avoids sharing issues.
			return constructModel(props, data);
		else
			// not found or unknown
			return new Area();
	}
	// Gets the next number from the tokenizer.
	private static float nextFloat(StringTokenizer str) {
		return Float.parseFloat(str.nextToken());
	}

	private CollisionModels() { }
}