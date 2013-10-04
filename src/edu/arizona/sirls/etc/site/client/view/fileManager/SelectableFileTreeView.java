package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.SelectableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileTreePresenter.Display;

public class SelectableFileTreeView extends Composite implements SelectableFileTreePresenter.Display {

	private FileTreeView fileTreeView;
	private Button closeButton;
	private Button selectButton;

	public SelectableFileTreeView() {
		fileTreeView = new FileTreeView();
		closeButton = new Button("Close");
		selectButton = new Button("Select");
		VerticalPanel verticalPanel = new VerticalPanel();
		ScrollPanel scrollPanel = new ScrollPanel(fileTreeView);
		verticalPanel.add(scrollPanel);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(selectButton);
		horizontalPanel.add(closeButton);
		verticalPanel.add(horizontalPanel);	
		this.initWidget(verticalPanel);
	}

	@Override
	public Button getSelectButton() {
		return this.selectButton;
	}


	@Override
	public Button getCloseButton() {
		return this.closeButton;
	}

	@Override
	public Display getFileTreeView() {
		return this.fileTreeView;
	}
	
}
