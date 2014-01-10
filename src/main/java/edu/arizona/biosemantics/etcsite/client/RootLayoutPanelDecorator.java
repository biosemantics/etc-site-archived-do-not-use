package edu.arizona.biosemantics.etcsite.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.MyActivityManager;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.layout.IEtcSiteView;

public class RootLayoutPanelDecorator {

	private IEtcSiteView etcSiteView;
	private ActivityManager menuActivityManager;
	private MyActivityManager contentActivityManager;
	private PlaceHistoryHandler placeHistoryHandler;
	private ActivityManager topActivityManager;

	@Inject
	public RootLayoutPanelDecorator(IEtcSiteView etcSiteView, 
			@Named("Top")ActivityManager topActivityManager,
			@Named("Menu")ActivityManager menuActivityManager,
			@Named("Content")MyActivityManager contentActivityManager,
			PlaceHistoryHandler placeHistoryHandler) {
		this.etcSiteView = etcSiteView; 
		this.topActivityManager = topActivityManager;
		this.menuActivityManager = menuActivityManager;
		this.contentActivityManager = contentActivityManager;
		this.placeHistoryHandler = placeHistoryHandler;
	}
	
	public void decorate(RootLayoutPanel rootLayoutPanel) {
		rootLayoutPanel.add(etcSiteView);
		topActivityManager.setDisplay(etcSiteView.getTopContainer());
		menuActivityManager.setDisplay(etcSiteView.getMenuContainer());
		contentActivityManager.setDisplay(etcSiteView.getContentContainer());
		placeHistoryHandler.handleCurrentHistory();
	}
	
}
