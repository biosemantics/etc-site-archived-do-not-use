package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.db.Task;

public interface ISemanticMarkupPreprocessView extends IsWidget {

	public interface Presenter {

		void setTask(Task task);

		IsWidget getView();

		void onNext();

		void onNextDescription();

		void onPreviousDescription();

		void onValueChange();
		
	}
	
	void setPresenter(Presenter presenter);

	String getHTML();

	void setBracketCounts(String bracketHTML);

	void setDescriptionIDLabel(String text);

	void setHTML(String text);

	void setEnabledNextDescriptionButton(boolean value);
	
	void setEnabledPreviousDescriptionButton(boolean value);
	
}
