package me.kiras.aimwhere.libraries.slick.state;

import me.kiras.aimwhere.libraries.slick.GameContainer;
import me.kiras.aimwhere.libraries.slick.Input;
import me.kiras.aimwhere.libraries.slick.SlickException;

/**
 * A simple state used an adapter so we don't have to implement all the event methods
 * every time.
 *
 * @author kevin
 */
public abstract class BasicGameState implements GameState {
	/**
	 * @see me.kiras.aimwhere.libraries.slick.ControlledInputReciever#inputStarted()
	 */
	public void inputStarted() {
		
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#isAcceptingInput()
	 */
	public boolean isAcceptingInput() {
		return true;
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#setInput(me.kiras.aimwhere.libraries.slick.Input)
	 */
	public void setInput(Input input) {
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#inputEnded()
	 */
	public void inputEnded() {
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.state.GameState#getID()
	 */
	public abstract int getID();

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerButtonPressed(int, int)
	 */
	public void controllerButtonPressed(int controller, int button) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerButtonReleased(int, int)
	 */
	public void controllerButtonReleased(int controller, int button) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerDownPressed(int)
	 */
	public void controllerDownPressed(int controller) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerDownReleased(int)
	 */
	public void controllerDownReleased(int controller) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerLeftPressed(int)
	 */
	public void controllerLeftPressed(int controller) {
		
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerLeftReleased(int)
	 */
	public void controllerLeftReleased(int controller) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerRightPressed(int)
	 */
	public void controllerRightPressed(int controller) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerRightReleased(int)
	 */
	public void controllerRightReleased(int controller) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerUpPressed(int)
	 */
	public void controllerUpPressed(int controller) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#controllerUpReleased(int)
	 */
	public void controllerUpReleased(int controller) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#keyPressed(int, char)
	 */
	public void keyPressed(int key, char c) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#keyReleased(int, char)
	 */
	public void keyReleased(int key, char c) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#mouseMoved(int, int, int, int)
	 */
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#mouseDragged(int, int, int, int)
	 */
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#mouseClicked(int, int, int, int)
	 */
	public void mouseClicked(int button, int x, int y, int clickCount) {
	}
	
	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#mousePressed(int, int, int)
	 */
	public void mousePressed(int button, int x, int y) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#mouseReleased(int, int, int)
	 */
	public void mouseReleased(int button, int x, int y) {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.state.GameState#enter(me.kiras.aimwhere.libraries.slick.GameContainer, me.kiras.aimwhere.libraries.slick.state.StateBasedGame)
	 */
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.state.GameState#leave(me.kiras.aimwhere.libraries.slick.GameContainer, me.kiras.aimwhere.libraries.slick.state.StateBasedGame)
	 */
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/**
	 * @see me.kiras.aimwhere.libraries.slick.InputListener#mouseWheelMoved(int)
	 */
	public void mouseWheelMoved(int newValue) {
	}

}
