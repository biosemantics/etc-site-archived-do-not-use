package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.presenter.LabelTextFieldConfirmPresenter;

public class LabelTextFieldConfirmView extends Composite implements LabelTextFieldConfirmPresenter.Display {

	private Label label = new Label();
	private TextBox textBox = new TextBox();
	private Button confirmButton = new Button("Confirm");
	
	public LabelTextFieldConfirmView() { 
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(label);
		verticalPanel.add(textBox);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
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
	public Button getConfirmButton() {
		return this.confirmButton;
	}	
}
