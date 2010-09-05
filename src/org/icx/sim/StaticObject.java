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

import java.awt.geom.*;

/**
 * Class which parents all static objects (pipes, lines, etc)
 * 
 * @author Stephen Carlson
 */
public abstract class StaticObject extends SimObject {
	// The cached transformed collision
	private Area transCol;

	public StaticObject(String image) {
		super(image);
		clearCollision();
	}

	// Clears the cached collision.
	protected void clearCollision() {
		transCol = null;
	}

	// Stores the transformed collision area
	public Area getTransformedCollision() {
		if (transCol == null)
			transCol = super.getTransformedCollision();
		return transCol;
	}
}