package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.review;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

public class OTOPopup extends TitleCloseDialogBox {

    public OTOPopup(String otoLink) { 
      // The popup's constructor's argument is a boolean specifying that it 
      // auto-close itself when the user clicks outside of it. 
      super(true, "Review Terms"); 
      //TODO load the OTO content here
      
      Frame frame = new Frame(otoLink);
      frame.addStyleName("otoFrame");
      this.add(frame);
    } 
}
