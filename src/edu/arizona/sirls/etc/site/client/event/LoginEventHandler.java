package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface LoginEventHandler extends EventHandler {
	void onLogin(LoginEvent loginEvent);
}
