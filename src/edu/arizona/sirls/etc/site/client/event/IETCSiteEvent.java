package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public interface IETCSiteEvent {

	public boolean requiresLogin();
	
	public HistoryState getHistoryState();
	
	public GwtEvent<?> getGwtEvent();
	
}
