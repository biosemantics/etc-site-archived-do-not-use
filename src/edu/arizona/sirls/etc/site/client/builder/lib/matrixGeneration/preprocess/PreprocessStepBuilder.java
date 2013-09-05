package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.preprocess;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.IStepBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.Step;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.learn.LearnStepBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;

public class PreprocessStepBuilder implements IStepBuilder {

	private final IFileAccessServiceAsync fileAccessService = GWT.create(IFileAccessService.class);
	private TextArea textArea = new TextArea();
	private Panel panel;
	private MatrixGenerationJob matrixGenerationJob;
	
	public PreprocessStepBuilder(MatrixGenerationJob matrixGenerationJob) {
		this.matrixGenerationJob = matrixGenerationJob;
	}
	
	@Override
	public void build(Panel panel) {
		this.panel = panel;
		String source = matrixGenerationJob.getTaxonDescriptionFile();
		fileAccessService.getFileContent(Authentication.getInstance().getAuthenticationToken(), source, fileContentCallback);
	}

	@Override
	public Step getStep() {
		return Step.PREPROCESS_TEXT;
	}
	
	protected AsyncCallback<String> fileContentCallback = new AsyncCallback<String>() {
		public void onSuccess(String result) {
			panel.add(new Label("Preprocess Text"));	
			textArea.setText(result);
			DOM.setElementAttribute(textArea.getElement(), "id", "preprocessTextArea");
			panel.add(textArea);
			
			Button correctButton = new Button("Correct");
			correctButton.addClickHandler(new CorrectButtonClickHandler(textArea, matrixGenerationJob));
			panel.add(correctButton);
			
			Button nextButton = new Button("Next");
			nextButton.addClickHandler(new ClickHandler() { 
				@Override
				public void onClick(ClickEvent event) { 
					PageBuilder pageBuilder = Session.getInstance().getPageBuilder();
					pageBuilder.setContentBuilder(new MatrixGenerationContentBuilder(new LearnStepBuilder(matrixGenerationJob)));
					pageBuilder.build();
				}
			});
			panel.add(nextButton);
		}

		public void onFailure(Throwable caught) {
			caught.printStackTrace();
		}
	};

}
