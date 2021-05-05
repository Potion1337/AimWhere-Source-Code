package me.kiras.aimwhere.libraries.slick.tests;

import me.kiras.aimwhere.libraries.slick.AppGameContainer;
import me.kiras.aimwhere.libraries.slick.BasicGame;
import me.kiras.aimwhere.libraries.slick.Color;
import me.kiras.aimwhere.libraries.slick.GameContainer;
import me.kiras.aimwhere.libraries.slick.Graphics;
import me.kiras.aimwhere.libraries.slick.Image;
import me.kiras.aimwhere.libraries.slick.Input;
import me.kiras.aimwhere.libraries.slick.SlickException;
import me.kiras.aimwhere.libraries.slick.geom.Polygon;
import me.kiras.aimwhere.libraries.slick.util.FastTrig;

/**
 * A simple graphics test for the context allowing vector based graphics
 *
 * @author kevin
 */
public class GraphicsTest extends BasicGame {
	/** True if we're clipping an area */
	private boolean clip;
	/** The angle of rotation */
	private float ang;
	/** The image being rendered */
	private Image image;
	/** A polygon to be rendered */
	private Polygon poly;
	/** The container holding this test */
	private GameContainer container;
	
	/**
	 * Create a new test of graphics context rendering
	 */
	public GraphicsTest() {
		super("Graphics Test");
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#init(me.kiras.aimwhere.libraries.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		image = new Image("testdata/logo.tga", true);
		
		Image temp = new Image("testdata/palette_tool.png");
		container.setMouseCursor(temp, 0, 0);
		
		container.setIcons(new String[] {"testdata/icon.tga"});
		container.setTargetFrameRate(100);
		
		poly = new Polygon();
		float len = 100;
		
		for (int x=0;x<360;x+=30) {
			if (len == 100) {
				len = 50; 
			} else {
				len = 100;
			}
			poly.addPoint((float) FastTrig.cos(Math.toRadians(x)) * len, 
						  (float) FastTrig.sin(Math.toRadians(x)) * len);
		}
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#render(me.kiras.aimwhere.libraries.slick.GameContainer, me.kiras.aimwhere.libraries.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setColor(Color.white);
		
		g.setAntiAlias(true);
		for (int x=0;x<360;x+=10) {
			g.drawLine(700,100,(int) (700+(Math.cos(Math.toRadians(x))*100)),
							   (int) (100+(Math.sin(Math.toRadians(x))*100)));
		}
		g.setAntiAlias(false);
		
		g.setColor(Color.yellow);
		g.drawString("The Graphics Test!", 300, 50);
		g.setColor(Color.white);
		g.drawString("Space - Toggles clipping", 400, 80);
		g.drawString("Frame rate capped to 100", 400, 120);
		
		if (clip) {
			g.setColor(Color.gray);
			g.drawRect(100,260,400,100);
			g.setClip(100,260,400,100);
		}

		g.setColor(Color.yellow);
		g.translate(100, 120);
		g.fill(poly);
		g.setColor(Color.blue);
		g.setLineWidth(3);
		g.draw(poly);
		g.setLineWidth(1);
		g.translate(0, 230);
		g.draw(poly);
		g.resetTransform();
		
		g.setColor(Color.magenta);
		g.drawRoundRect(10, 10, 100, 100, 10);
		g.fillRoundRect(10, 210, 100, 100, 10);
		
		g.rotate(400, 300, ang);
		g.setColor(Color.green);
		g.drawRect(200,200,200,200);
		g.setColor(Color.blue);
		g.fillRect(250,250,100,100);

		g.drawImage(image, 300,270);
		
		g.setColor(Color.red);
		g.drawOval(100,100,200,200);
		g.setColor(Color.red.darker());
		g.fillOval(300,300,150,100);
		g.setAntiAlias(true);
		g.setColor(Color.white);
		g.setLineWidth(5.0f);
		g.drawOval(300,300,150,100);
		g.setAntiAlias(true);
		g.resetTransform();
		
		if (clip) {
			g.clearClip();
		}
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#update(me.kiras.aimwhere.libraries.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		ang += delta * 0.1f;
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_ESCAPE) {
			net.minecraftforge.fml.common.FMLCommonHandler.instance().exitJava(0, true);
		}
		if (key == Input.KEY_SPACE) {
			clip = !clip;
		}
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments passed to the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new GraphicsTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
