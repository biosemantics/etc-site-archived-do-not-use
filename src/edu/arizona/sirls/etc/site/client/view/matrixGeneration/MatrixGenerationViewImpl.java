package edu.arizona.sirls.etc.site.client.view.matrixGeneration;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.arizona.sirls.etc.site.client.view.ImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.TaskStageEnum;

public abstract class MatrixGenerationViewImpl extends Composite {
	
	@UiField(provided=true)
	HorizontalPanel submenuPanel;

	public MatrixGenerationViewImpl() {
		submenuPanel = new HorizontalPanel();
		int i=0;
		for(TaskStageEnum step : TaskStageEnum.values()) {
			ImageLabelComposite stepEntry = new ImageLabelComposite("images/Enumeration_unselected_" + i++ + ".gif", "20px", "20px", step.displayName());
			if(getStep().equals(step))
				stepEntry = new ImageLabelComposite("images/Enumeration_" + i++ + ".gif", "20px", "20px", step.displayName());
			stepEntry.addStyleName("submenuEntry");
			submenuPanel.add(stepEntry);
		}
	}

	protected abstract TaskStageEnum getStep();
}
