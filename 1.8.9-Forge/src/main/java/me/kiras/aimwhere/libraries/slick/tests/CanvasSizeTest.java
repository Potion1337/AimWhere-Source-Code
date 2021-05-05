package me.kiras.aimwhere.libraries.slick.tests;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import me.kiras.aimwhere.libraries.slick.BasicGame;
import me.kiras.aimwhere.libraries.slick.CanvasGameContainer;
import me.kiras.aimwhere.libraries.slick.GameContainer;
import me.kiras.aimwhere.libraries.slick.Graphics;
import me.kiras.aimwhere.libraries.slick.SlickException;
import me.kiras.aimwhere.libraries.slick.util.Log;

/**
 * Quick test to confirm canvas size is reported correctly
 * 
 * @author kevin
 */
public class CanvasSizeTest extends BasicGame {
	
	/**
	 * Create test
	 */
	public CanvasSizeTest() {
		super("Test");
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#init(me.kiras.aimwhere.libraries.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		System.out.println(container.getWidth() + ", " + container.getHeight());
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.Game#render(me.kiras.aimwhere.libraries.slick.GameContainer, me.kiras.aimwhere.libraries.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g)
			throws SlickException {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#update(me.kiras.aimwhere.libraries.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta)
			throws SlickException {
	}

	/**
	 * Entry point to the test
	 * 
	 * @param args The command line arguments passed in (none honoured)
	 */
	public static void main(String[] args) {
		try {
			CanvasGameContainer container = new CanvasGameContainer(
					new CanvasSizeTest());
			container.setSize(640,480);
			Frame frame = new Frame("Test");
			frame.setLayout(new GridLayout(1,2));
			frame.add(container);
			frame.pack();
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					net.minecraftforge.fml.common.FMLCommonHandler.instance().exitJava(0, true);
				}
			});
			frame.setVisible(true);
	
			container.start();
		} catch (Exception e) {
			Log.error(e);
		}
	}
}
