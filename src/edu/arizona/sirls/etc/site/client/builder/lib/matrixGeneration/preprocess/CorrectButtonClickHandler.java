package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.preprocess;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.TextArea;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.api.file.ISetFileContentAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.file.SetFileContentAsyncCallback;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class CorrectButtonClickHandler implements ClickHandler, ISetFileContentAsyncCallbackListener {

	private TextArea textArea;
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);

	public CorrectButtonClickHandler(TextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void onClick(ClickEvent event) {
		String newText = textArea.getText();
		SetFileContentAsyncCallback callback = new SetFileContentAsyncCallback();
		callback.addListener(this);
		String target = MatrixGenerationJob.getInstance().getTaxonDescriptionFile();
		fileService.setFileContent(Authentication.getInstance().getAuthenticationToken(), target, newText, callback);
	}

	@Override
	public void notifyResult(boolean result) {
		if(!result)
			System.out.println("for some reason the corrected text could not be set as file content on the server side; false was returned");
	}

	@Override
	public void notifyException(Throwable caught) {
		caught.printStackTrace();
	}

}
