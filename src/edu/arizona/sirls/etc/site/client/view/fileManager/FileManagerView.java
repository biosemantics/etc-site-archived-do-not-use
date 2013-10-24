package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileManagerPresenter;

public class FileManagerView extends Composite implements FileManagerPresenter.Display {
	
	private HTMLPanel htmlPanel;
	private Anchor markupReviewAnchor;
	private SimplePanel fileManagerPanel;
	
	public FileManagerView() {
		this.htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='fileManagerContent'></div></div>");
		VerticalPanel verticalPanel = new VerticalPanel();
		Label headingLabel = new Label("File Manager");
		headingLabel.addStyleName("siteHeading");
		verticalPanel.add(headingLabel);
		fileManagerPanel = new SimplePanel();
		verticalPanel.add(fileManagerPanel);
		markupReviewAnchor = new Anchor("Review Markup");
		verticalPanel.add(markupReviewAnchor);
		
		this.htmlPanel.add(verticalPanel, "fileManagerContent");
		this.initWidget(htmlPanel);
	}

	@Override
	public void addFileTreeView(Widget fileTreeView) {
		fileManagerPanel.add(fileTreeView);
	}

	@Override
	public Anchor getMarkupReviewAnchor() {
		return this.markupReviewAnchor;
	}

	@Override
	public SimplePanel getFileManagerPanel() {
		return this.fileManagerPanel;
	}
	
}
