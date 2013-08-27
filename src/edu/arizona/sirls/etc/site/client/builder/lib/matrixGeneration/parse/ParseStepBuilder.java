package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.parse;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

import edu.arizona.sirls.etc.site.client.Session;
import edu.arizona.sirls.etc.site.client.builder.PageBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.IStepBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.Step;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.output.OutputStepBuilder;

public class ParseStepBuilder implements IStepBuilder {
	
	private MatrixGenerationJob matrixGenerationJob;

	public ParseStepBuilder(MatrixGenerationJob matrixGenerationJob) { 
		this.matrixGenerationJob = matrixGenerationJob;
	}
	
	@Override
	public void build(Panel panel) {
		panel.add(new Label("Parse Text"));
	
		panel.add(new Label("The system is parsing text."));
		panel.add(new Label("When the process is completed you will receive an email."));
		
		Button nextButton = new Button("Next");
		nextButton.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				PageBuilder pageBuilder = Session.getInstance().getPageBuilder();
				pageBuilder.setContentBuilder(new MatrixGenerationContentBuilder(new OutputStepBuilder(matrixGenerationJob)));
				pageBuilder.build();
			}
		});
		panel.add(nextButton);
	}

	@Override
	public Step getStep() {
		return Step.PARSE_TEXT;
	}

}
