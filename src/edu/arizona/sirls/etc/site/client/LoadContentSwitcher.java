package edu.arizona.sirls.etc.site.client;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;

public class LoadContentSwitcher {

	public void setLoading() {
		DOM.getElementById("loading").getStyle().clearDisplay();
		DOM.getElementById("top").getStyle().setDisplay(Display.NONE);
		DOM.getElementById("container").getStyle().setDisplay(Display.NONE);
		DOM.getElementById("footer").getStyle().setDisplay(Display.NONE);
	}
	
	public void setContent() {
		DOM.getElementById("loading").getStyle().setDisplay(Display.NONE);
		DOM.getElementById("top").getStyle().clearDisplay();
		DOM.getElementById("container").getStyle().clearDisplay();
		DOM.getElementById("footer").getStyle().clearDisplay();
	}
	
}
