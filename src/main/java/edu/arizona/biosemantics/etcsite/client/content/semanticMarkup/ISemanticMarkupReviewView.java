package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.oto2.oto.client.Oto;

public interface ISemanticMarkupReviewView extends IsWidget {

	public interface Presenter {

		void setTask(Task task);

		ISemanticMarkupReviewView getView();

		void onNext();

		void onSendToOto();
		
	}
	
	void setPresenter(Presenter presenter);

	//void setFrameUrl(String string);
	
	void setReview(int collectionId, String secret);

	Oto getOto();

	void setEnabledSendToOto(boolean b);
	
}
