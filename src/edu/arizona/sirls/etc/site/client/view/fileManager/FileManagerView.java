package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileManagerPresenter;

public class FileManagerView extends Composite implements FileManagerPresenter.Display {
	
	private HTMLPanel htmlPanel;
	
	public FileManagerView() {
		this.htmlPanel = new HTMLPanel("<div class='content900pxCentered'>" +
				"<div id='fileManagerContent'></div></div>");
		this.initWidget(htmlPanel);
	}

	@Override
	public void addFileTreeView(Widget fileTreeView) {
		this.htmlPanel.add(fileTreeView, "fileManagerContent");
	}
	
}
