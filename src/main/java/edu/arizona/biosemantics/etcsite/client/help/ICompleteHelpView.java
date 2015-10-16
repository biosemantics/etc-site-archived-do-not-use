package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.user.client.ui.IsWidget;

public interface ICompleteHelpView extends IsWidget {

	public interface Presenter {

		void onGetStart();
		
	}

	void setPresenter(Presenter presenter);
	
}
