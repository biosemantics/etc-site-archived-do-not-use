package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;

public interface ISettingsView extends IsWidget {

	public interface Presenter {
		void onSave();

		void onPasswordSave();
		
		void onNewOTOGoogleAccount();

		void onNewOTOAccount(String text, String text2);

		void onSaveOTOAccount(boolean share, String text, String text2);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	
	void setData(ShortUser user);
	ShortUser getData();
	String getCurrentPassword();
	String getNewPassword();
	String getConfirmPassword();
	void setOTOAccount(String email, String password);
	void setLinkedOTOAccount(String email);
}
