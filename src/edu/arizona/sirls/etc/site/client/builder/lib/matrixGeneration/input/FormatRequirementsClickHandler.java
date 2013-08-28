package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;

import edu.arizona.sirls.etc.site.client.builder.dialog.MessageDialogBox;

public class FormatRequirementsClickHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {	
		MessageDialogBox dialogBox = new MessageDialogBox("Format Requirements");
		
		dialogBox.setResponse("Matrix Generation requires a set of XML files as input. The XML files have to be " +
				"<ul>" +
				"<li> valid against the specified XML <a target=\"_blank\" href=\"https://github.com/biosemantics/charaparser/blob/master/resources/io/iplant.xsd\">schema</a>" +
				"<li>  UTF-8 encoded" +
				"</ul>");
		dialogBox.center(); 
		dialogBox.show(); 
		dialogBox.setCloseButtonFocus(true);
	}

}
