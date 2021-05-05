package me.kiras.aimwhere.libraries.slick.gui;

import me.kiras.aimwhere.libraries.slick.Graphics;
import me.kiras.aimwhere.libraries.slick.SlickException;

/**
 * Renamed to provide backwards compatibility
 *
 * @author kevin
 * @deprecated
 */
public abstract class BasicComponent extends AbstractComponent {
	/** The x position of the component */
	protected int x;
	/** The y position of the component */
	protected int y;
	/** The width of the component */
	protected int width;
	/** The height of the component */
	protected int height;

	/**
	 * Create a new component
	 * 
	 * @param container
	 *            The container displaying this component
	 */
	public BasicComponent(GUIContext container) {
		super(container);
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.gui.AbstractComponent#getHeight()
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.gui.AbstractComponent#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.gui.AbstractComponent#getX()
	 */
	public int getX() {
		return x;
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.gui.AbstractComponent#getY()
	 */
	public int getY() {
		return y;
	}

	/**
	 * Allow the sub-component to render
	 * 
	 * @param container The container holding the GUI
	 * @param g The graphics context into which we should render
	 */
	public abstract void renderImpl(GUIContext container, Graphics g);
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.gui.AbstractComponent#render(me.kiras.aimwhere.libraries.slick.gui.GUIContext, me.kiras.aimwhere.libraries.slick.Graphics)
	 */
	public void render(GUIContext container, Graphics g) throws SlickException {
		renderImpl(container,g);
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.gui.AbstractComponent#setLocation(int, int)
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

}
