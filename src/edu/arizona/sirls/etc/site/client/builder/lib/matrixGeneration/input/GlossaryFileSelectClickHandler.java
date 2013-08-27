package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatServiceAsync;

public class GlossaryFileSelectClickHandler extends FileSelectClickHandler {

	private final IFileFormatServiceAsync fileFormatService = GWT.create(IFileFormatService.class);

	@Override
	public void onClick(ClickEvent event) {
		String target = this.getSelectedTarget();
		fileFormatService.isValidGlossary(Authentication.getInstance().getAuthenticationToken(), target, validFileCallback);
	}
	
}
