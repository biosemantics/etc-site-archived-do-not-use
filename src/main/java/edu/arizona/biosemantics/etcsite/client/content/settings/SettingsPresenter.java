package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.inject.Inject;

public class SettingsPresenter implements SettingsView.Presenter {
	
	private SettingsView view;

	@Inject
	public SettingsPresenter(SettingsView view) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public void onSubmit() {
		//service do change; return if worked or not
		view.getOldPassword();
		view.getNewPassword();
		view.getConfirmedNewPassword();
		view.getBioportalUserId();
		view.getBioportalAPIKey();
	}

}
