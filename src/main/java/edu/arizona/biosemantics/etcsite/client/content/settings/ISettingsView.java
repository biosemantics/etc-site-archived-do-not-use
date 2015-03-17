package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;

public interface ISettingsView extends IsWidget {

	public interface Presenter {
		void onSubmit();
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	String getFirstName();
	String getLastName();
	String getOpenIdProviderId();
	String getEmail();
	String getAffiliation();
	String getBioportalUserId();
	String getBioportalAPIKey();
	String getOldPassword();
	String getNewPassword();
	String getConfirmNewPassword();
	String getOpenIdProvider();
	
	void setData(ShortUser user);
	void setErrorMessage(String str);
	void clearPasswords();
	
	boolean isMatrixGenerationEmailChecked();
	boolean isTreeGenerationEmailChecked();
	boolean isTextCaptureEmailChecked();
	boolean isTaxonomyComparisonEmailChecked();
	
}
