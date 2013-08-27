package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.PopupPanel;

import edu.arizona.sirls.etc.site.client.widget.FileTreeAndMenuComposite;

public class FileManagerPopup extends PopupPanel {

    public FileManagerPopup() { 
        // The popup's constructor's argument is a boolean specifying that it 
        // auto-close itself when the user clicks outside of it. 
        super(true); 
        
        FileTreeAndMenuComposite fileTreeAndMenuComposite = new FileTreeAndMenuComposite(true);
        fileTreeAndMenuComposite.addStyleName("fileSelect");
        this.add(fileTreeAndMenuComposite);
      } 	
	
}
