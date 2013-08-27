package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.output;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.IStepBuilder;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration.Step;

public class OutputStepBuilder implements IStepBuilder {

	private MatrixGenerationJob matrixGenerationJob;

	public OutputStepBuilder(MatrixGenerationJob matrixGenerationJob) { 
		this.matrixGenerationJob = matrixGenerationJob;
	}
	
	@Override
	public void build(Panel panel) {
		panel.add(new Label("Output"));
		
		HorizontalPanel outputPanel = new HorizontalPanel();
		outputPanel.add(new Button("Select Output File"));
		outputPanel.add(new Label("filename"));
		outputPanel.add(new Button("Save"));
		panel.add(outputPanel);
		
	}
	
	@Override
	public Step getStep() {
		return Step.OUTPUT;
	}

}
