package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.presenter.LabelTextFieldCancelConfirmPresenter;

public class LabelTextFieldCancelConfirmView extends Composite implements LabelTextFieldCancelConfirmPresenter.Display {

	private Label label = new Label();
	private TextBox textBox = new TextBox();
	private Button cancelButton = new Button("Cancel");
	private Button confirmButton = new Button("Confirm");
	
	public LabelTextFieldCancelConfirmView() { 
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(label);
		verticalPanel.add(textBox);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(cancelButton);
		horizontalPanel.add(confirmButton);
		verticalPanel.add(horizontalPanel);
		this.initWidget(verticalPanel);
	}

	@Override
	public Label getLabel() {
		return this.label;
	}

	@Override
	public TextBox getTextBox() {
		return this.textBox;
	}

	@Override
	public Button getCancelButton() {
		return this.cancelButton;
	}

	@Override
	public Button getConfirmButton() {
		return this.confirmButton;
	}	
}
