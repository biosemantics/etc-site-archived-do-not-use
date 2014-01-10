package edu.arizona.biosemantics.etcsite.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISetupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.Setup;

public class Client implements EntryPoint {

	private final ClientGinjector injector = GWT.create(ClientGinjector.class);

	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	      public void onUncaughtException(Throwable e) {
	        //log.log(Level.SEVERE, e.getMessage(), e);
	      }
	    });
		
		ISetupServiceAsync setupService = injector.getSetupService();
		setupService.getSetup(new RPCCallback<Setup>() {
			@Override
			public void onResult(Setup result) {
				ServerSetup.getInstance().setSetup(result);
				
				//init layout
				RootLayoutPanel rootLayoutPanel = RootLayoutPanel.get();
				injector.getRootLayoutPanelDecorator().decorate(rootLayoutPanel);
			}
		});
	}
}
