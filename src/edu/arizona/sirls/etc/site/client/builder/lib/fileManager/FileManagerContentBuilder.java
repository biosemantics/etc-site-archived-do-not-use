package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;
import edu.arizona.sirls.etc.site.client.widget.FileTreeAndMenuComposite;

public class FileManagerContentBuilder implements IContentBuilder {
	
	@Override
	public void build() {
		createHTML();
		initWidgets();
	}

	private void initWidgets() {
		RootPanel panel = RootPanel.get("fileManagerContent");
		panel.clear();
		FileTreeAndMenuComposite fileTree = new FileTreeAndMenuComposite(true);
		panel.add(fileTree);
	}

	private void createHTML() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'><div id='fileManagerContent'></div></div>");
	}
}
