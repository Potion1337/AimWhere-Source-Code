package me.kiras.aimwhere.libraries.slick;

/**
 * Description of classes capable of responding to controller events
 * 
 * @author kevin
 */
public interface ControllerListener extends ControlledInputReciever {

	/**
	 * Notification that the left control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 */
	void controllerLeftPressed(int controller);

	/**
	 * Notification that the left control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 */
	void controllerLeftReleased(int controller);

	/**
	 * Notification that the right control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 */
	void controllerRightPressed(int controller);

	/**
	 * Notification that the right control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 */
	void controllerRightReleased(int controller);

	/**
	 * Notification that the up control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 */
	void controllerUpPressed(int controller);

	/**
	 * Notification that the up control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 */
	void controllerUpReleased(int controller);

	/**
	 * Notification that the down control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 */
	void controllerDownPressed(int controller);

	/**
	 * Notification that the down control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 */
	void controllerDownReleased(int controller);

	/**
	 * Notification that a button control has been pressed on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was pressed.
	 * @param button The index of the button pressed (starting at 1)
	 */
	void controllerButtonPressed(int controller, int button);

	/**
	 * Notification that a button control has been released on 
	 * the controller.
	 * 
	 * @param controller The index of the controller on which the control
	 * was released.
	 * @param button The index of the button released (starting at 1)
	 */
	void controllerButtonReleased(int controller, int button);

}