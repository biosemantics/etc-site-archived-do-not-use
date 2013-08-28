package edu.arizona.sirls.etc.site.client.builder.lib.fileManager;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.builder.IContentBuilder;
import edu.arizona.sirls.etc.site.client.builder.dialog.LoadingPopup;
import edu.arizona.sirls.etc.site.client.widget.FileTreeAndMenuComposite;
import edu.arizona.sirls.etc.site.client.widget.ILoadListener;
import edu.arizona.sirls.etc.site.shared.rpc.FileFilter;

public class FileManagerContentBuilder implements IContentBuilder, ILoadListener {
	
	private FileTreeAndMenuComposite fileTree;
	private LoadingPopup loadingPopup;
	
	@Override
	public void build() {
		createHTML();
		initWidgets();
	}

	private void initWidgets() {
		loadingPopup = new LoadingPopup();
		loadingPopup.center();
		loadingPopup.show(); 
		
		RootPanel panel = RootPanel.get("fileManagerContent");
		panel.clear();
		fileTree = new FileTreeAndMenuComposite(true, FileFilter.ALL);
		fileTree.addLoadListener(this);
		
	}

	private void createHTML() {
		Element content = DOM.getElementById("content");
		content.setInnerHTML("<div class='content900pxCentered'><div id='fileManagerContent'></div></div>");
	}

	@Override
	public void notifyLoadFinished(Widget widget) {
		loadingPopup.hide();
		RootPanel panel = RootPanel.get("fileManagerContent");
		panel.add(fileTree);
	}
}
