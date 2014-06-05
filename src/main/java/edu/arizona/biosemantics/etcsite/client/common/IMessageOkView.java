package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.IsWidget;

public interface IMessageOkView extends IsWidget {

	public interface Presenter {

		void onOk();

		void show();

		void setMessage(String message);

	}
	
	void setPresenter(Presenter presenter);
	
	public void setMessage(String message);
}
