package edu.arizona.biosemantics.etcsite.client.content.fileManager;

import com.google.gwt.user.client.ui.IsWidget;

public interface IFileManagerView extends IsWidget {

	public interface Presenter {

		void onAnnotationReview();

		IsWidget getView();

		void refresh();

	}

	void setPresenter(Presenter presenter);
	
}
