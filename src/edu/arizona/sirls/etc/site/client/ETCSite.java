package edu.arizona.sirls.etc.site.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

import edu.arizona.sirls.etc.site.client.presenter.LoggedInHeaderPresenter;
import edu.arizona.sirls.etc.site.client.presenter.LoggedOutHeaderPresenter;
import edu.arizona.sirls.etc.site.client.view.LoggedInHeaderView;
import edu.arizona.sirls.etc.site.client.view.LoggedOutHeaderView;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ETCSite implements EntryPoint {
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {		
		
//		LoadContentSwitcher loadContentSwitcher = new LoadContentSwitcher();
//		loadContentSwitcher.setContent();
//		
//		LoggedInHeaderView loggedIn = new LoggedInHeaderView();
//		LoggedInHeaderPresenter presenter = new LoggedInHeaderPresenter(null, loggedIn);
//		presenter.go(RootPanel.get("header"));
//		
//		LoggedOutHeaderView loggedOut = new LoggedOutHeaderView();
//		LoggedOutHeaderPresenter presenter2 = new LoggedOutHeaderPresenter(null, loggedOut, null);
//		presenter2.go(RootPanel.get("footer"));
		
	    HandlerManager eventBus = new HandlerManager(null);
	    SitePresenter presenter = new MySitePresenter(eventBus);
	    
	    LoadContentSwitcher loadContentSwitcher = new LoadContentSwitcher();
	    loadContentSwitcher.setContent();
	    
		presenter.go(RootPanel.get("header"), RootPanel.get("menu"), 
	    		RootPanel.get("content"), RootPanel.get("footer"));
	}	
}
