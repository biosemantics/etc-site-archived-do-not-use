package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.IsWidget;

public interface ITextInputView extends IsWidget {

	public interface Presenter {

		void show(String title, String text, String defaultTextBoxText, ITextInputListener listener);
		
		void onConfirm(String text);

		void onCancel();
		
	}

	void setPresenter(Presenter presenter);

	void setText(String text);

	void setTextBox(String defaultTextBoxText);

	String getTextBox();
	
	
	
}
