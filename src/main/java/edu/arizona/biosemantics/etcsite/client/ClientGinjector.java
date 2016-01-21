package edu.arizona.biosemantics.etcsite.client;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

import edu.arizona.biosemantics.etcsite.core.shared.rpc.setup.ISetupServiceAsync;

@GinModules(ClientModule.class)
public interface ClientGinjector extends Ginjector {

	RootLayoutPanelDecorator getRootLayoutPanelDecorator();

	ISetupServiceAsync getSetupService();
	
}