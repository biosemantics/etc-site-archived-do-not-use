package edu.arizona.sirls.etc.site.client.view;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;

public interface SettingsView {

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
