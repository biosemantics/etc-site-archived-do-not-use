package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.preprocess;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;

public class CorrectButtonClickHandler implements ClickHandler {

	private TextArea textArea;
	private final IFileAccessServiceAsync fileAccessService = GWT.create(IFileAccessService.class);
	private MatrixGenerationJob matrixGenerationJob;

	public CorrectButtonClickHandler(TextArea textArea, MatrixGenerationJob matrixGenerationJob) {
		this.textArea = textArea;
		this.matrixGenerationJob = matrixGenerationJob;
	}

	@Override
	public void onClick(ClickEvent event) {
		String newText = textArea.getText();

		String target = matrixGenerationJob.getTaxonDescriptionFile();
		fileAccessService.setFileContent(Authentication.getInstance().getAuthenticationToken(), target, newText, setFileContentCallback);
	}
	
	protected AsyncCallback<Boolean> setFileContentCallback = new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean result) {
			if(!result)
				System.out.println("for some reason the corrected text could not be set as file content on the server side; false was returned");
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};

}
