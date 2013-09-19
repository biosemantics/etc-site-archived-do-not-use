package edu.arizona.sirls.etc.site.client.view.treeGeneration;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import edu.arizona.sirls.etc.site.client.presenter.treeGeneration.TreeGenerationPresenter;

public class TreeGenerationView extends Composite implements TreeGenerationPresenter.Display {

	public TreeGenerationView() { 
		HTMLPanel htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"Here are the tree generation steps</div>");
		this.initWidget(htmlPanel);
	}

}
