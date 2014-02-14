package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.IsWidget;

public interface ILoginView extends IsWidget {

	public interface Presenter {

		void onLogin();

		void onCancel();

		void show(ILoginListener listener);
		
	}
	
	public interface ILoginListener {
		void onLogin();
		void onLoginFailure();
	}
	
	void setPresenter(Presenter presenter);

	String getUsername();

	String getPassword();
	
}
