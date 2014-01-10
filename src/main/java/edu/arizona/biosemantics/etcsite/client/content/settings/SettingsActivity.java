package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class SettingsActivity extends MyAbstractActivity implements ISettingsView.Presenter {

	private ISettingsView settingsView;
	private PlaceController placeController;

	@Inject
	public SettingsActivity(ISettingsView settingsView, PlaceController placeController) {
		this.placeController = placeController;
		this.settingsView = settingsView;
	}
	
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		settingsView.setPresenter(this);
		panel.setWidget(settingsView.asWidget());
	}

	@Override
	public void onSubmit() {
		//TODO
	}


	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
