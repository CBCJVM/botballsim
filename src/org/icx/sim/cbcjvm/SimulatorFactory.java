/*
 * This file is part of CBCJVM.
 * CBCJVM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CBCJVM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CBCJVM.  If not, see <http://www.gnu.org/licenses/>.
 */

package cbccore.low;

import cbccore.Device;

/**
 * Subclasses of this should override the getNewSimulator() method. This class
 * is what gets passed to Device to initialize a custom simulator. The purpose
 * of making this a factory is to hold off any potentially wasteful
 * initialization until after deciding if we are on the actual device or not.
 * 
 * @author Benjamin Woodruff
 * 
 */

public abstract class SimulatorFactory {
	
	public SimulatorFactory() {
		// nothing
	}
	
	public abstract Simulator getNewSimulator();
}









