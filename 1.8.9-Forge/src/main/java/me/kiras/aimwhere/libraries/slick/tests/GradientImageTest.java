package me.kiras.aimwhere.libraries.slick.tests;

import me.kiras.aimwhere.libraries.slick.AppGameContainer;
import me.kiras.aimwhere.libraries.slick.BasicGame;
import me.kiras.aimwhere.libraries.slick.Color;
import me.kiras.aimwhere.libraries.slick.GameContainer;
import me.kiras.aimwhere.libraries.slick.Graphics;
import me.kiras.aimwhere.libraries.slick.Image;
import me.kiras.aimwhere.libraries.slick.Input;
import me.kiras.aimwhere.libraries.slick.SlickException;
import me.kiras.aimwhere.libraries.slick.fills.GradientFill;
import me.kiras.aimwhere.libraries.slick.geom.Polygon;
import me.kiras.aimwhere.libraries.slick.geom.Rectangle;
import me.kiras.aimwhere.libraries.slick.geom.Shape;

/**
 * A test for applying gradients to images
 *
 * @author kevin
 */
public class GradientImageTest extends BasicGame {
	/** The first image loaded */
	private Image image1;
	/** The second image loaded */
	private Image image2;
	/** The gradient paint we'll apply */
	private GradientFill fill;
	/** The shape we'll blend across */
	private Shape shape;
	/** The shape we'll blend across */
	private Polygon poly;
	/** The container for the test */
	private GameContainer container;
	/** The angle of rotation */
	private float ang;
	/** True if we're rotating */
	private boolean rotating = false;
	
	/**
	 * Create a new image rendering test
	 */
	public GradientImageTest() {
		super("Gradient Image Test");
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#init(me.kiras.aimwhere.libraries.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		this.container = container;
		
		image1 = new Image("testdata/grass.png");
		image2 = new Image("testdata/rocks.png");
		
		fill = new GradientFill(-64,0,new Color(1,1,1,1f),64,0,new Color(0,0,0,0));
		shape = new Rectangle(336,236,128,128);
	    poly = new Polygon();
		poly.addPoint(320,220);
		poly.addPoint(350,200);
		poly.addPoint(450,200);
		poly.addPoint(480,220);
		poly.addPoint(420,400);
		poly.addPoint(400,390);
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#render(me.kiras.aimwhere.libraries.slick.GameContainer, me.kiras.aimwhere.libraries.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		g.drawString("R - Toggle Rotationg",10,50);
		g.drawImage(image1, 100, 236);
		g.drawImage(image2, 600, 236);
		
		g.translate(0, -150);
		g.rotate(400, 300, ang);
		g.texture(shape, image2);
		g.texture(shape, image1, fill);
		g.resetTransform();
		
		g.translate(0, 150);
		g.rotate(400, 300, ang);
		g.texture(poly, image2);
		g.texture(poly, image1, fill);
		g.resetTransform();
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#update(me.kiras.aimwhere.libraries.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
		if (rotating) {
			ang += (delta * 0.1f);
		}
	}

	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new GradientImageTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
		if (key == Input.KEY_R) {
			rotating = !rotating;
		}
		if (key == Input.KEY_ESCAPE) {
			container.exit();
		}
	}
}
