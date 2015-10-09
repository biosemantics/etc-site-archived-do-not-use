package edu.arizona.biosemantics.etcsite.help;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Yet another test
 */
public class Help implements EntryPoint {

	private final HelpGinjector injector = GWT.create(HelpGinjector.class);

	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	      public void onUncaughtException(Throwable e) {
	        //log.log(Level.SEVERE, e.getMessage(), e);
	      }
	    });
		
		RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
		HelpRootLayoutPanelDecorator decorator = injector.getRootLayoutPanelDecorator();
		decorator.decorate(rootLayoutPanel);
		 
	}
}
