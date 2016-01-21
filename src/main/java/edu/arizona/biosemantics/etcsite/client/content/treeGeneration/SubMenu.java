package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.common.ImageLabel;
import edu.arizona.biosemantics.etcsite.core.shared.model.treegeneration.TaskStageEnum;

public class SubMenu extends Composite {

	private static SubmenuUiBinder uiBinder = GWT.create(SubmenuUiBinder.class);

	interface SubmenuUiBinder extends UiBinder<Widget, SubMenu> {
	}
	
	@UiField
	ImageLabel createInput;
	
	@UiField
	ImageLabel input;
	
	@UiField
	ImageLabel view;
	
	public SubMenu() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setStep(TaskStageEnum taskStage) {
		switch(taskStage) {
		case CREATE_INPUT:
			createInput.setImage("images/Enumeration_1.gif");
			break;
		case INPUT:
			input.setImage("images/Enumeration_2.gif");
			break;
		case VIEW:
			view.setImage("images/Enumeration_3.gif");
			break;
		default:
			input.setImage("images/Enumeration_1.gif");
		}
	}

}
