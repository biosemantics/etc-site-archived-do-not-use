package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.core.shared.model.ShortUser;

public interface ISettingsView extends IsWidget {

	public interface Presenter {
		void onSave();

		void onPasswordSave();
		
		void onNewOTOGoogleAccount();

		void onNewOTOAccount(String email, String password, String passwordConfirm);

		void onSaveOTOAccount(boolean share, String email, String password);

		void onLinkAccount(Boolean share, String email, String password);

		void onExistingOTOGoogleAccount();
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
	
	boolean isMatrixGenerationEmailChecked();
	boolean isTreeGenerationEmailChecked();
	boolean isTextCaptureEmailChecked();
	boolean isTaxonomyComparisonEmailChecked();
	boolean isLinkedOTOAccount();
}
