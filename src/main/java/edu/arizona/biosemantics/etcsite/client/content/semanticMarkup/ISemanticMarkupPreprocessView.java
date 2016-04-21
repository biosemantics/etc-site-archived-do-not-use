package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.Task;

public interface ISemanticMarkupPreprocessView extends IsWidget {

	public interface Presenter {

		void setTask(Task task);

		IsWidget getView();

		void onNext();

		void onNextDescription();

		void onPreviousDescription();

		void onValueChange();

		void setDescriptionSummary(String unmatchDescription, String correctedDescription);
		
	}
	
	void setPresenter(Presenter presenter);

	String getHTML();

	void setHTML(String text);
	
	void setBracketCounts(String bracketHTML);

	void setEnabledNextDescriptionButton(boolean value);
	
	void setEnabledPreviousDescriptionButton(boolean value);

	void setDescriptionSummaryLabel(String text);
	
	void setCurrentDescriptionLabel(String text);
}
