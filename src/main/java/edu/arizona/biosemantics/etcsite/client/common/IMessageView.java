package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.IsWidget;

public interface IMessageView extends IsWidget {

	public interface Presenter {
		void showMessage(String title, String html);
		void onClose();
		
	}
	
	void setHtml(String html);
	
	void setPresenter(Presenter presenter);
	
}
