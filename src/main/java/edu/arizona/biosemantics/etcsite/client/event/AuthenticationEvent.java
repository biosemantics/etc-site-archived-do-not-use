package edu.arizona.biosemantics.etcsite.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent.AuthenticationEventHandler;

public class AuthenticationEvent extends GwtEvent<AuthenticationEventHandler> {

	public interface AuthenticationEventHandler extends EventHandler {
		public void onAuthentication(AuthenticationEvent event);
	}
	
	public static Type<AuthenticationEventHandler> TYPE = new Type<AuthenticationEventHandler>();

	public static enum AuthenticationEventType {
		LOGGEDIN, LOGGEDOUT, TO_BE_DETERMINED
	}

	private AuthenticationEventType type;
	
	public AuthenticationEvent(AuthenticationEventType type) {
		this.type = type;
	}

	@Override
	public GwtEvent.Type<AuthenticationEventHandler> getAssociatedType() {
		return TYPE;
	}


	@Override
	protected void dispatch(AuthenticationEventHandler handler) {
		handler.onAuthentication(this);
	}

	public AuthenticationEventType getType() {
		return type;
	}	

	

}
