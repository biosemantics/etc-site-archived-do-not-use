package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.client.common.MessageConfirmPresenter.IConfirmListener;

public interface IMessageConfirmView extends IsWidget {

	public interface Presenter { 
		void onConfirm();
		void onCancel();
		void show(String title, String message, String cancelText,
				String confirmText, IConfirmListener listener);
		void show(String title, String message, IConfirmListener listener);
	}
	
	void setHtmlMessage(String htmlMessage);

	void setPresenter(Presenter presenter);

	void setConfirmText(String confirmText);

	void setCancelText(String cancelText);
	
	
	
}
