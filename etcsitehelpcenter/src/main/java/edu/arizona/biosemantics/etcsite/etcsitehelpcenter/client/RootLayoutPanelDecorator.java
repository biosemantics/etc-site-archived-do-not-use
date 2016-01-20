package edu.arizona.biosemantics.etcsitehelpcenter.client;

import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsitehelpcenter.client.layout.IEtcSiteHelpView;

public class RootLayoutPanelDecorator {

	private IEtcSiteHelpView etcSiteHelpView;
	/*private MyActivityManager contentActivityManager;
	private PlaceHistoryHandler placeHistoryHandler;
	private ActivityManager helpActivityManager;
*/
	@Inject
	public RootLayoutPanelDecorator(IEtcSiteHelpView.Presenter etcSiteHelpPresenter 
			/*@Named("Content")MyActivityManager contentActivityManager,
			@Named("Help")ActivityManager helpActivityManager,
			PlaceHistoryHandler placeHistoryHandler*/) {
		this.etcSiteHelpView = etcSiteHelpPresenter.getView(); 
		/*this.helpActivityManager = helpActivityManager;
		this.contentActivityManager = contentActivityManager;
		this.placeHistoryHandler = placeHistoryHandler;*/
	}
	
	public void decorate(RootLayoutPanel rootLayoutPanel) {
		rootLayoutPanel.add(etcSiteHelpView);
		/*contentActivityManager.setDisplay(etcSiteView.getContentContainer());
		helpActivityManager.setDisplay(etcSiteView.getHelpContainer());
		placeHistoryHandler.handleCurrentHistory();*/
	}
	
}
