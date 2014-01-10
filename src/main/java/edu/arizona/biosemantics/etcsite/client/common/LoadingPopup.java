package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoadingPopup extends PopupPanel {
	
    public LoadingPopup() { 
        super(false, true); 
        DOM.setIntStyleAttribute(this.getElement(), "zIndex", 2147483647);

        this.add(new Label("Loading..."));
        
        //loading task would have to be split into pieces because only one thread manages gif animation and js execution, hence image will 'freeze' otherwise
        //https://groups.google.com/forum/#!topic/google-web-toolkit/cqAVEpwGH0s
        //https://groups.google.com/forum/#!topic/Google-Web-Toolkit/xC40RsTI4E4
        //this.add(new Image("images/loader1.gif"));
    }

	public void start() {
		this.center();
		this.show(); 
	} 
	
	public void stop() { 
		this.hide();
	}
}
