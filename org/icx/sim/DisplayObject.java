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
import java.awt.image.*;
import java.util.*;

/**
 * A class that displays an image. Borrowed from the AP GridWorld with
 *  heavy modification.
 */
public class DisplayObject {
	private Map<Color, Image> tintedVersions;
	private Image image;
	private Location location;
	private Color tint;
	private int setWidth;
	private int setHeight;

	/**
	 * Constructs an object that knows how to display an image. Looks for the
	 * named file first in the jar file, then in the current directory.
	 * 
	 * @param imageFilename name of file containing image, WITHOUT the extension
	 */
	public DisplayObject(String imageFilename) {
		tintedVersions = new HashMap<Color, Image>();
		image = Simulator.getIcon(imageFilename).getImage();
		tint = null;
		setWidth = -1; setWidth = -1;
	}

	/**
	 * Sets the tint color.
	 * 
	 * @param color the new tint color
	 */
	public void setColor(Color color) {
		tint = color;
	}

	/**
	 * Gets the tint color, or null if it is not tinted.
	 * 
	 * @return the tint color
	 */
	public Color getColor() {
		return tint;
	}

	/**
	 * Coerces the object width to setWidth.
	 * 
	 * @param setWidth the force object width; -1 restores the image default.
	 */
	public void setWidth(int setWidth) {
		this.setWidth = setWidth;
	}

	/**
	 * Coerces the object height to setHeight.
	 * 
	 * @param setHeight the force object height; -1 restores the image default.
	 */
	public void setHeight(int setHeight) {
		this.setHeight = setHeight;
	}

	// Displayed dimensions of the image
	public int getSetWidth() {
		return setWidth;
	}

	public int getSetHeight() {
		return setHeight;
	}

	// Actual dimensions of the image
	public int getHeight() {
		return image.getHeight(null);
	}

	public int getWidth() {
		return image.getWidth(null);
	}

	// Square bounding box hit check for object
	//  good only for GUI, use precision shapes on items and java.awt.Area for better detection.
	public boolean hit(int x, int y) {
		return Math.abs(x - location.getX()) < getWidth() / 2 &&
			Math.abs(y - location.getY()) < getHeight() / 2;
	}

	/**
	 * Draws an object. This implementation draws an
	 * object by tinting, scaling, and rotating the image in the image file.
	 * 
	 * @param comp the Component on which to draw
	 * @param g2 the Graphics2D on which to draw
	 */
	public void draw(Component comp, Graphics2D g2) {
		Image tinted;
		if (tint != null) {
			tinted = tintedVersions.get(tint);
			if (tinted == null) {
				// generate new tinted version
				FilteredImageSource src = new FilteredImageSource(image.getSource(),
					TintFilter.forColor(tint));
				tinted = comp.createImage(src);
				tintedVersions.put(tint, tinted);
				// load it up
				MediaTracker track = new MediaTracker(comp);
				track.addImage(tinted, 0);
				try {
					track.waitForID(0);
				} catch (Exception e) { }
			}
		} else tinted = image; // else use existing tinted version
		int height = tinted.getHeight(null);
		if (setHeight > 0) height = setHeight;
		int width = tinted.getWidth(null);
		if (setWidth > 0) width = setWidth;
		// draw image scaled, moved, and rotated
		Graphics2D g = (Graphics2D) g2.create();
		g.translate(location.getX() * RobotConstants.MM_TO_PIXELS,
			location.getY() * RobotConstants.MM_TO_PIXELS);
		g.rotate(location.getTheta());
		g.drawImage(tinted, -width / 2, -height / 2, width, height, null);
		g.dispose();
	}

	/**
	 * An image filter class that tints colors.
	 */
	private static class TintFilter extends RGBImageFilter {
		/**
		 * Gets a tint filter for the given color.
		 * 
		 * @param color the color to filter
		 * @return the tint filter
		 */
		protected static TintFilter forColor(Color color) {
			TintFilter ret = pregen.get(color);
			if (ret == null) {
				ret = new TintFilter(color);
				pregen.put(color, ret);
			}
			return ret;
		}

		// Caches filters and filtered images
		private static Map<Color, TintFilter> pregen = new HashMap<Color, TintFilter>();

		// Components of the tinted image
		private int tintR;
		private int tintG;
		private int tintB;

		/**
		 * Constructs an image filter for tinting colors in an image.
		 * @param color the tint color
		 */
		private TintFilter(Color color) {
			canFilterIndexColorModel = true;
			int rgb = color.getRGB();
			tintR = (rgb >> 16) & 0xff;
			tintG = (rgb >> 8) & 0xff;
			tintB = rgb & 0xff;
		}

		public int filterRGB(int x, int y, int argb) {
			// Separate pixel into its RGB coomponents.
			int alpha = (argb >> 24) & 0xff;
			int red = (argb >> 16) & 0xff;
			int green = (argb >> 8) & 0xff;
			int blue = argb & 0xff;
			// Use NTSC/PAL algorithm to convert RGB to gray level
			//double lum = (0.2989 * red + 0.5866 * green + 0.1144 * blue) / 255;

			// interpolate between tint and pixel color. Pixels with
			// gray level 0.5 are colored with the tint color,
			// white and black pixels stay unchanged.
			// We use a quadratic interpolation function
			// f(x) = 1 - 4 * (x - 0.5)^2 that has
			// the property f(0) = f(1) = 0, f(0.5) = 1

			// Note: Julie's algorithm used a linear interpolation
			// function f(x) = min(2 - 2 * x, 2 * x);
			// and it interpolated between tint and 
			// (lum < 0.5 ? black : white)

			//double scale = 1 - (4 * ((lum - 0.5) * (lum - 0.5)));
			double scale = 0.3;
			red = (int) (tintR * scale + red * (1 - scale));
			green = (int) (tintG * scale + green * (1 - scale));
			blue = (int) (tintB * scale + blue * (1 - scale));
			return (alpha << 24) | (red << 16) | (green << 8) | blue;
		}
	}

	/**
	 * Sets the location of the object.
	 * 
	 * @param location the location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Gets the location of the object.
	 * 
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
}