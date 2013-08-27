package edu.arizona.sirls.etc.site.client.builder.lib.matrixGeneration;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.widget.ImageLabelComposite;

public interface IStepBuilder {

	public void build(Panel panel);
	
	public Object getStep();
	
}
