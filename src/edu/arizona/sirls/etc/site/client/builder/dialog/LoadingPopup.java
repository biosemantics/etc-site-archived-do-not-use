package edu.arizona.sirls.etc.site.client.builder.dialog;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;


public class LoadingPopup extends PopupPanel {
	
    public LoadingPopup() { 
        super(false, true); 
        this.add(new Image("images/loader1.gif"));
      } 
}
