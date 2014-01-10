package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.common.ImageLabel;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum;

public class SubMenu extends Composite {

	private static SubmenuUiBinder uiBinder = GWT.create(SubmenuUiBinder.class);

	interface SubmenuUiBinder extends UiBinder<Widget, SubMenu> {
	}
	
	@UiField
	ImageLabel input;
	
	@UiField
	ImageLabel preprocess;
	
	@UiField
	ImageLabel learn;
	
	@UiField
	ImageLabel review;
	
	@UiField
	ImageLabel parse;
	
	@UiField
	ImageLabel output;

	public SubMenu() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setStep(TaskStageEnum taskStage) {
		switch(taskStage) {
		case INPUT:
			input.setImage("images/Enumeration_1.gif");
			break;
		case PREPROCESS_TEXT:
			preprocess.setImage("images/Enumeration_2.gif");
			break;
		case LEARN_TERMS:
			learn.setImage("images/Enumeration_3.gif");
			break;
		case REVIEW_TERMS:
			review.setImage("images/Enumeration_4.gif");
			break;
		case PARSE_TEXT:
			parse.setImage("images/Enumeration_5.gif");
			break;
		case OUTPUT:
			output.setImage("images/Enumeration_6.gif");
			break;
		default:
			input.setImage("images/Enumeration_1.gif");
		}
	}

}
