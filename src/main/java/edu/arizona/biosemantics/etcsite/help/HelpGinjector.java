package edu.arizona.biosemantics.etcsite.help;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(HelpModule.class)
public interface HelpGinjector extends Ginjector {

	HelpRootLayoutPanelDecorator getRootLayoutPanelDecorator();
	
}