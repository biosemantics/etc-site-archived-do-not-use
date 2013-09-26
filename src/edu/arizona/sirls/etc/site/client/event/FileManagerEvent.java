package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.client.HistoryState;

public class FileManagerEvent extends GwtEvent<FileManagerEventHandler> implements ETCSiteEvent {

	public static Type<FileManagerEventHandler> TYPE = new Type<FileManagerEventHandler>();
	
	@Override
	public Type<FileManagerEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FileManagerEventHandler handler) {
		handler.onFileManager(this);
	}

	@Override
	public boolean requiresLogin() {
		return true;
	}

	@Override
	public HistoryState getHistoryState() {
		return HistoryState.FILE_MANAGER;
	}

	@Override
	public GwtEvent<?> getGwtEvent() {
		return this;
	}

}
