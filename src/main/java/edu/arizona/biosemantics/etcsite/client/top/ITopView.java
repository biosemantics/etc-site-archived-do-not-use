package edu.arizona.biosemantics.etcsite.client.top;

import com.google.gwt.user.client.ui.IsWidget;

public interface ITopView extends IsWidget {
	
	public interface Presenter {
		void onFileManager();
		void onTaskManager();
		void onHome();
		void onSettings();
		void onHelp();
		void onLogout();
	}

	void setPresenter(Presenter presenter);

	void setGreeting(String text);

}
