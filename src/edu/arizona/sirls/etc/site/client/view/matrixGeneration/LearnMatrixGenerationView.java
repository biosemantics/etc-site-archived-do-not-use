package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.LearnMatrixGenerationPresenter;
import edu.arizona.sirls.etc.site.client.view.ListItem;
import edu.arizona.sirls.etc.site.client.view.OrderedList;
import edu.arizona.sirls.etc.site.shared.rpc.Step;

public class LearnMatrixGenerationView extends MatrixGenerationView implements LearnMatrixGenerationPresenter.Display {
	
	private Button nextButton;
	private ListItem sentencesItem;
	private ListItem wordsItem;

	@Override
	protected Step getStep() {
		return Step.LEARN_TERMS;
	}

	@Override
	protected Widget getStepWidget() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName("contentPanel");
		panel.add(new Label("Learn Terms"));
		panel.add(new Label("There are"));
		OrderedList orderedList = new OrderedList();
		this.wordsItem = new ListItem();
		this.wordsItem.setText("x words");
		this.sentencesItem = new ListItem();
		this.sentencesItem.setText("x sentences");
		orderedList.add(wordsItem);
		orderedList.add(sentencesItem);
		panel.add(orderedList);
		panel.add(new Label("in this collection."));
		panel.add(new Label("Charaparser is learning the terminology. You will receive an email when processing has completed. If you wish, you can come back to this task via the " +
				"task manager"));
		
		this.nextButton = new Button("Next");
		panel.add(nextButton);
		return panel;
	}

	@Override
	public Button getNextButton() {
		return this.nextButton;
	}

	@Override
	public void setSentences(int sentences) {
		this.sentencesItem.setText(sentences + " sentences");
	}

	@Override
	public void setWords(int words) {
		this.wordsItem.setText(words + " words");
	}

}
