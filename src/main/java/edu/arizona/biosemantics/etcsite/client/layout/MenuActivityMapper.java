package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.menu.MenuActivity;
import edu.arizona.biosemantics.etcsite.client.menu.MenuPlace;
import edu.arizona.biosemantics.etcsite.client.menu.StartMenuActivity;
import edu.arizona.biosemantics.etcsite.client.menu.StartMenuPlace;

public class MenuActivityMapper implements ActivityMapper {
	
	private MenuActivity menuActivity;
	private StartMenuActivity startMenuActivity;
	private Activity currentActivity;
	private IEtcSiteView.Presenter etcSitePresenter;

	@Inject
	public MenuActivityMapper(MenuActivity menuActivity, StartMenuActivity startMenuActivity, IEtcSiteView.Presenter etcSitePresenter) {
		this.menuActivity = menuActivity;
		this.startMenuActivity = startMenuActivity;
		this.etcSitePresenter = etcSitePresenter;
	}
	
	@Override
	public Activity getActivity(Place place) {
		if(currentActivity == null)
			currentActivity = startMenuActivity;
		
		if(place instanceof MenuPlace) {
			currentActivity = menuActivity;
			//100 for expanded, 38 for collapsed
			etcSitePresenter.setHeaderSize(38, false);
		}
		if(place instanceof StartMenuPlace) {
			currentActivity = startMenuActivity;
			etcSitePresenter.setHeaderSize(163, false);
		}
		
		return currentActivity;
	}

}
