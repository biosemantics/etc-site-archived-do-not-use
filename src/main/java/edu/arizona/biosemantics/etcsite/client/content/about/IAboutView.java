package edu.arizona.biosemantics.etcsite.client.content.about;

import com.google.gwt.user.client.ui.IsWidget;

public interface IAboutView extends IsWidget {

	public interface Presenter {

		void onGetStart();
		
	}

	void setPresenter(Presenter presenter);
	
}
