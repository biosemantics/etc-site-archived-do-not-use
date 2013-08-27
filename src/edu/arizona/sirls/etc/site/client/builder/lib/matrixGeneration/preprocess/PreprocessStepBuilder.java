package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.preprocess;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.api.file.GetFileContentAsyncCallback;
import edu.arizona.sirls.etc.site.client.api.file.IGetFileContentAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.IStepBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.Step;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.learn.LearnStepBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;

public class PreprocessStepBuilder implements IStepBuilder, IGetFileContentAsyncCallbackListener {

	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private TextArea textArea = new TextArea();
	private Panel panel;
	
	@Override
	public void build(Panel panel) {
		this.panel = panel;
		String source = MatrixGenerationJob.getInstance().getTaxonDescriptionFile();
		GetFileContentAsyncCallback callback = new GetFileContentAsyncCallback();
		callback.addListener(this);
		fileService.getFileContent(Authentication.getInstance().getAuthenticationToken(), source, callback);
	}

	@Override
	public Step getStep() {
		return Step.PREPROCESS_TEXT;
	}

	@Override
	public void notifyResult(String result) {
		panel.add(new Label("Preprocess Text"));	
		textArea.setText(result);
		DOM.setElementAttribute(textArea.getElement(), "id", "preprocessTextArea");
		panel.add(textArea);
		
		Button correctButton = new Button("Correct");
		correctButton.addClickHandler(new CorrectButtonClickHandler(textArea));
		panel.add(correctButton);
		
		Button nextButton = new Button("Next");
		nextButton.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				PageBuilder pageBuilder = Session.getInstance().getPageBuilder();
				pageBuilder.setContentBuilder(MatrixGenerationContentBuilder.getInstance(new LearnStepBuilder()));
				pageBuilder.build();
			}
		});
		panel.add(nextButton);
	}

	@Override
	public void notifyException(Throwable caught) {
		caught.printStackTrace();
	}

}
