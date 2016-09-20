package edu.arizona.biosemantics.etcsite.client.content.gettingstarted;

import com.google.gwt.user.client.ui.IsWidget;

public interface IGettingStartedView extends IsWidget {

	public interface Presenter {

		void onHome();
		
	}

	void setPresenter(Presenter presenter);
	
}
