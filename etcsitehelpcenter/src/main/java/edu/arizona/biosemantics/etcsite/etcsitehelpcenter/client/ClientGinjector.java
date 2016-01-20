package edu.arizona.biosemantics.etcsite.etcsitehelpcenter.client;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(ClientModule.class)
public interface ClientGinjector extends Ginjector {

	RootLayoutPanelDecorator getRootLayoutPanelDecorator();
	
}