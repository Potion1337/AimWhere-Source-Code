package me.kiras.aimwhere.libraries.slick.tests;

import me.kiras.aimwhere.libraries.slick.AppGameContainer;
import me.kiras.aimwhere.libraries.slick.BasicGame;
import me.kiras.aimwhere.libraries.slick.CachedRender;
import me.kiras.aimwhere.libraries.slick.Color;
import me.kiras.aimwhere.libraries.slick.GameContainer;
import me.kiras.aimwhere.libraries.slick.Graphics;
import me.kiras.aimwhere.libraries.slick.Input;
import me.kiras.aimwhere.libraries.slick.SlickException;

/**
 * A simple test to show performance gains from cache operations in situtations where
 * rendering is static and heavy
 * 
 * @author kevin
 */
public class CachedRenderTest extends BasicGame {
	/** The set of operations to be cached */
	private Runnable operations;
	/** The cached version of the operations */
	private CachedRender cached;
	/** True if we're drawing the cached version */
	private boolean drawCached;
	
	/**
	 * Create a new simple test for cached rendering (aka display lists)
	 */
	public CachedRenderTest() {
		super("Cached Render Test");
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#init(me.kiras.aimwhere.libraries.slick.GameContainer)
	 */
	public void init(final GameContainer container) throws SlickException {
		operations = new Runnable() {
			public void run() {
				for (int i=0;i<100;i++) {
					int c = i+100;
					container.getGraphics().setColor(new Color(c,c,c,c));
					container.getGraphics().drawOval((i*5)+50,(i*3)+50,100,100);
				}
			}
		};
		
		cached = new CachedRender(operations);
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#update(me.kiras.aimwhere.libraries.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) throws SlickException {
		if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
			drawCached = !drawCached;
		}
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.Game#render(me.kiras.aimwhere.libraries.slick.GameContainer, me.kiras.aimwhere.libraries.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setColor(Color.white);
		g.drawString("Press space to toggle caching", 10, 130);
		if (drawCached) {
			g.drawString("Drawing from cache", 10, 100);
			cached.render();
		} else {
			g.drawString("Drawing direct", 10, 100);
			operations.run();
		}
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new CachedRenderTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
