package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.IsWidget;

public interface IMessageConfirmView extends IsWidget {

	public interface Presenter { 
		void onConfirm();
		void onCancel();
		void show(String title, String message, String cancelText,
				String confirmText, IConfirmListener listener);
		void show(String title, String message, IConfirmListener listener);
	}
	
	public interface IConfirmListener {
		public void onConfirm();
		public void onCancel();
	}
	
	void setHtmlMessage(String htmlMessage);

	void setPresenter(Presenter presenter);

	void setConfirmText(String confirmText);

	void setCancelText(String cancelText);
	
	
	
}
