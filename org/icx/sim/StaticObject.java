package org.icx.sim;

/**
 * Class which parents all static objects (pipes, lines, etc)
 * 
 * @author Stephen Carlson
 */
public abstract class StaticObject extends SimObject {
	public StaticObject(String image) {
		super(image);
	}
}