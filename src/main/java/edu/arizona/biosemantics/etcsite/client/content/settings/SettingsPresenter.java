package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.inject.Inject;

public class SettingsPresenter implements SettingsView.Presenter {
	
	private SettingsView view;

	//Note: This class appears to be unused. See SettingsActivity.java for settings functionality.
	
	@Inject
	public SettingsPresenter(SettingsView view) {
		this.view = view;
		view.setPresenter(this);
	}

	@Override
	public void onSubmit() {}

}
