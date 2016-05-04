package edu.arizona.biosemantics.etcsitehelp.shared.help;

import com.google.gwt.core.client.JavaScriptObject;

public class HelpContent extends JavaScriptObject{

	protected HelpContent() {
		// TODO Auto-generated constructor stub
	}
	
	public final native String getTitle() /*-{ return this.title;}-*/;
	public final native String getContent() /*-{ return this.content;}-*/;
	
	
}
