package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import com.google.gwt.user.client.ui.Button;

import gwtupload.client.IFileInput.ButtonFileInput;

/**
 * The ButtonFileInput implementation sets the button invisble once an upload is active.
 * As this is not the desired behavior for us this class overrides the default behavior.
 * @author rodenhausen
 */
public class MyFileInput extends ButtonFileInput {

	public MyFileInput(Button addButton) {
		super(addButton);
	}

	@Override
	public void setVisible(boolean b) {
		//ignore visibility based on active upload
	}

}
