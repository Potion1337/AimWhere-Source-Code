package me.kiras.aimwhere.libraries.slick.tests;

import me.kiras.aimwhere.libraries.slick.AppGameContainer;
import me.kiras.aimwhere.libraries.slick.GameContainer;
import me.kiras.aimwhere.libraries.slick.SlickException;
import me.kiras.aimwhere.libraries.slick.state.StateBasedGame;
import me.kiras.aimwhere.libraries.slick.tests.states.TestState1;
import me.kiras.aimwhere.libraries.slick.tests.states.TestState2;
import me.kiras.aimwhere.libraries.slick.tests.states.TestState3;

/**
 * A test for the multi-state based functionality
 *
 * @author kevin
 */
public class StateBasedTest extends StateBasedGame {

	/**
	 * Create a new test
	 */
	public StateBasedTest() {
		super("State Based Test");
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.state.StateBasedGame#initStatesList(me.kiras.aimwhere.libraries.slick.GameContainer)
	 */
	public void initStatesList(GameContainer container) {
		addState(new TestState1());
		addState(new TestState2());
		addState(new TestState3());
	}
	
	/**
	 * Entry point to our test
	 * 
	 * @param argv The arguments to pass into the test
	 */
	public static void main(String[] argv) {
		try {
			AppGameContainer container = new AppGameContainer(new StateBasedTest());
			container.setDisplayMode(800,600,false);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
