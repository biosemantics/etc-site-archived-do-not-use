package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class FileManagerEvent extends GwtEvent<FileManagerEventHandler> {

	public static Type<FileManagerEventHandler> TYPE = new Type<FileManagerEventHandler>();
	
	@Override
	public Type<FileManagerEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FileManagerEventHandler handler) {
		handler.onFileManager(this);
	}

}
