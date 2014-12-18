package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.user.client.ui.IsWidget;

public interface IImportOtoView extends IsWidget {

	public interface Presenter {

		IImportOtoView getView();

		void onImport(String termCategorization, String synonymy);

	}

	void setPresenter(IImportOtoView.Presenter presenter);

	void show();

	void hide();
	
}
