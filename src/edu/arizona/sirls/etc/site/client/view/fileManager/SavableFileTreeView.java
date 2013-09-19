package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.SavableFileTreePresenter;

public class SavableFileTreeView extends Composite implements SavableFileTreePresenter.Display {

	private FileTreeView fileTreeView;
	private TextBox nameTextBox;
	private Button closeButton;
	
	public SavableFileTreeView() {
		fileTreeView = new FileTreeView();
		closeButton = new Button("Save");
		VerticalPanel verticalPanel = new VerticalPanel();
		ScrollPanel scrollPanel = new ScrollPanel(fileTreeView);
		verticalPanel.add(scrollPanel);
		nameTextBox = new TextBox();
		verticalPanel.add(nameTextBox);
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(closeButton);
		verticalPanel.add(horizontalPanel);	
		this.initWidget(verticalPanel);
	}
	
	@Override
	public Button getCloseButton() {
		return this.closeButton;
	}

	@Override
	public FileTreeView getFileTreeView() {
		return this.fileTreeView;
	}
	
	@Override
	public TextBox getNameTextBox() { 
		return this.nameTextBox;
	}

}
