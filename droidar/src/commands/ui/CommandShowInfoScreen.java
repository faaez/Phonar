package commands.ui;

import gui.simpleUI.EditItem;
import gui.simpleUI.SimpleUI;
import android.app.Activity;

import commands.Command;

public class CommandShowInfoScreen extends Command {

	private Activity myCurrentActivity;
	private EditItem myObjectToDisplay;
	private Object myOptionalMessage;

	public CommandShowInfoScreen(Activity currentActivity, EditItem objectToEdit) {
		myCurrentActivity = currentActivity;
		myObjectToDisplay = objectToEdit;
	}

	public CommandShowInfoScreen(Activity currentActivity,
			EditItem objectToEdit, Object optionalMessage) {
		myCurrentActivity = currentActivity;
		myObjectToDisplay = objectToEdit;
		myOptionalMessage = optionalMessage;
	}

	@Override
	public boolean execute() {
		SimpleUI.showInfoScreen(myCurrentActivity, myObjectToDisplay,
				myOptionalMessage);
		return true;
	}
}
