package edu.arizona.sirls.etc.site.client.widget;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.shared.rpc.FileFilter;

public class FileTreeAndSelectComposite extends Composite {

	private FileTreeComposite tree; 
	
	public FileTreeAndSelectComposite(boolean enableDragAndDrop, FileFilter fileFilter, ClickHandler selectClickHandler, ClickHandler closeClickHandler) {
		tree = new FileTreeComposite(enableDragAndDrop, fileFilter);
		tree.refresh();
		
		Button closeButton = new Button("Close");
		Button selectButton = new Button("Select");
		VerticalPanel verticalPanel = new VerticalPanel();
		ScrollPanel scrollPanel = new ScrollPanel(tree);
		verticalPanel.add(scrollPanel);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(closeButton);
		horizontalPanel.add(selectButton);
		verticalPanel.add(horizontalPanel);
		
		selectButton.addClickHandler(selectClickHandler); 
		closeButton.addClickHandler(closeClickHandler); 
		
		this.initWidget(verticalPanel);
	}

	public FileTreeComposite getFileTreeComposite() {
		return tree;
	}
	
}
