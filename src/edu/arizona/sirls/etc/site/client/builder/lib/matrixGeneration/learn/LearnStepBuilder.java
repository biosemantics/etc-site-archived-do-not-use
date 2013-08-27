package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.learn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.api.matrixGeneration.IProcessJobAsyncCallbackListener;
import edu.arizona.sirls.etc.site.client.api.matrixGeneration.ProcessJobAsyncCallback;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.IStepBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.Step;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.review.ReviewStepBuilder;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.LearnInvocation;

public class LearnStepBuilder implements IStepBuilder, IProcessJobAsyncCallbackListener {

	private final IMatrixGenerationServiceAsync matrixGenerationService = GWT.create(IMatrixGenerationService.class);
	private Panel panel;
	
	@Override
	public void build(Panel panel) {
		this.panel = panel;
		ProcessJobAsyncCallback processJobCallback = new ProcessJobAsyncCallback();
		processJobCallback.addListener(this);
		matrixGenerationService.learn(Authentication.getInstance().getAuthenticationToken(),
				MatrixGenerationJob.getInstance(), processJobCallback);
	}

	@Override
	public Step getStep() {
		return Step.LEARN_TERMS;
	}

	@Override
	public void notifyResult(LearnInvocation result) {
		panel.add(new Label("Learn Terms"));
		panel.add(new Label("There are"));
		panel.add(new Label(result.getSentences() + " words"));
		panel.add(new Label(result.getWords() + " sentences"));
		panel.add(new Label("in this collection."));
		panel.add(new Label("Charaparser is learning the terminology. You will receive an email when processing has completed. If you wish, you can come back to this task via the " +
				"task manager"));
		
		Button nextButton = new Button("Next");
		nextButton.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				PageBuilder pageBuilder = Session.getInstance().getPageBuilder();
				pageBuilder.setContentBuilder(MatrixGenerationContentBuilder.getInstance(ReviewStepBuilder.getInstance()));
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
