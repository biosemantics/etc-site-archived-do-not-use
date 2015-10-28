package edu.arizona.biosemantics.etcsite.client;

import com.google.gwt.activity.shared.MyActivityManager;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.layout.IEtcSiteView;

public class RootLayoutPanelDecorator {

	private IEtcSiteView etcSiteView;
	private MyActivityManager contentActivityManager;
	private PlaceHistoryHandler placeHistoryHandler;
	private MyActivityManager helpActivityManager;

	@Inject
	public RootLayoutPanelDecorator(IEtcSiteView.Presenter etcSitePresenter, 
			@Named("Content")MyActivityManager contentActivityManager,
			@Named("Help")MyActivityManager helpActivityManager,
			PlaceHistoryHandler placeHistoryHandler) {
		this.etcSiteView = etcSitePresenter.getView(); 
		this.helpActivityManager = helpActivityManager;
		this.contentActivityManager = contentActivityManager;
		this.placeHistoryHandler = placeHistoryHandler;
	}
	
	public void decorate(RootLayoutPanel rootLayoutPanel) {
		rootLayoutPanel.add(etcSiteView);
		contentActivityManager.setDisplay(etcSiteView.getContentContainer());
		helpActivityManager.setDisplay(etcSiteView.getHelpContainer());
		placeHistoryHandler.handleCurrentHistory();
	}
	
}
