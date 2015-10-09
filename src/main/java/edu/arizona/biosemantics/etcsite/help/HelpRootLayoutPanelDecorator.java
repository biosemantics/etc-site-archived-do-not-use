package edu.arizona.biosemantics.etcsite.help;

import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.help.layout.IEtcSiteHelpView;

public class HelpRootLayoutPanelDecorator {

	private IEtcSiteHelpView etcSiteHelpView;
	/*private MyActivityManager contentActivityManager;
	private PlaceHistoryHandler placeHistoryHandler;
	private ActivityManager helpActivityManager;
*/
	@Inject
	public HelpRootLayoutPanelDecorator(IEtcSiteHelpView.Presenter etcSiteHelpPresenter 
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
