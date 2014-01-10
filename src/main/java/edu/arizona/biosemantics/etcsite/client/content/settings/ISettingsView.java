package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.user.client.ui.Widget;

public interface ISettingsView {

	public interface Presenter {
		void onSubmit();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	String getConfirmedNewPassword();
	String getOldPassword();
	String getNewPassword();
	String getBioportalUserId();
	String getBioportalAPIKey();

}
