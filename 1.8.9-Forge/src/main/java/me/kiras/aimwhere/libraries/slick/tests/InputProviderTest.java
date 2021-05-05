package me.kiras.aimwhere.libraries.slick.tests;

import me.kiras.aimwhere.libraries.slick.AppGameContainer;
import me.kiras.aimwhere.libraries.slick.BasicGame;
import me.kiras.aimwhere.libraries.slick.GameContainer;
import me.kiras.aimwhere.libraries.slick.Graphics;
import me.kiras.aimwhere.libraries.slick.Input;
import me.kiras.aimwhere.libraries.slick.SlickException;
import me.kiras.aimwhere.libraries.slick.command.BasicCommand;
import me.kiras.aimwhere.libraries.slick.command.Command;
import me.kiras.aimwhere.libraries.slick.command.ControllerButtonControl;
import me.kiras.aimwhere.libraries.slick.command.ControllerDirectionControl;
import me.kiras.aimwhere.libraries.slick.command.InputProvider;
import me.kiras.aimwhere.libraries.slick.command.InputProviderListener;
import me.kiras.aimwhere.libraries.slick.command.KeyControl;
import me.kiras.aimwhere.libraries.slick.command.MouseButtonControl;

/**
 * A test for abstract input via InputProvider
 *
 * @author kevin
 */
public class InputProviderTest extends BasicGame implements InputProviderListener {
	/** The command for attack */
	private Command attack = new BasicCommand("attack");
	/** The command for jump */
	private Command jump = new BasicCommand("jump");
	/** The command for jump */
	private Command run = new BasicCommand("run");
	/** The input provider abstracting input */
	private InputProvider provider;
	/** The message to be displayed */
	private String message = "";
	
	/**
	 * Create a new image rendering test
	 */
	public InputProviderTest() {
		super("InputProvider Test");
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#init(me.kiras.aimwhere.libraries.slick.GameContainer)
	 */
	public void init(GameContainer container) throws SlickException {
		provider = new InputProvider(container.getInput());
		provider.addListener(this);
		
		provider.bindCommand(new KeyControl(Input.KEY_LEFT), run);
		provider.bindCommand(new KeyControl(Input.KEY_A), run);
		provider.bindCommand(new ControllerDirectionControl(0, ControllerDirectionControl.LEFT), run);
		provider.bindCommand(new KeyControl(Input.KEY_UP), jump);
		provider.bindCommand(new KeyControl(Input.KEY_W), jump);
		provider.bindCommand(new ControllerDirectionControl(0, ControllerDirectionControl.UP), jump);
		provider.bindCommand(new KeyControl(Input.KEY_SPACE), attack);
		provider.bindCommand(new MouseButtonControl(0), attack);
		provider.bindCommand(new ControllerButtonControl(0, 1), attack);
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#render(me.kiras.aimwhere.libraries.slick.GameContainer, me.kiras.aimwhere.libraries.slick.Graphics)
	 */
	public void render(GameContainer container, Graphics g) {
		g.drawString("Press A, W, Left, Up, space, mouse button 1,and gamepad controls",10,50);
		g.drawString(message,100,150);
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.BasicGame#update(me.kiras.aimwhere.libraries.slick.GameContainer, int)
	 */
	public void update(GameContainer container, int delta) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.command.InputProviderListener#controlPressed(me.kiras.aimwhere.libraries.slick.command.Command)
	 */
	public void controlPressed(Command command) {
		message = "Pressed: "+command;
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.command.InputProviderListener#controlReleased(me.kiras.aimwhere.libraries.slick.command.Command)
	 */
	public void controlReleased(Command command) {
		message = "Released: "+command;
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new InputProviderTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
