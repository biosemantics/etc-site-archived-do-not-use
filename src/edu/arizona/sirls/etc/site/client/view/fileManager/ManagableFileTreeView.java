package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.ManagableFileTreePresenter;
import gwtupload.client.IUploadStatus;
import gwtupload.client.Uploader;

//can't subclass FileTreeView sa initWidget may only be called once
public class ManagableFileTreeView extends Composite implements ManagableFileTreePresenter.Display{
	
	private FileTreeView fileTreeView;
	private MyUploader uploader;
	private Button deleteButton;
	private Button renameButton;
	private Button createDirectoryButton;


	public ManagableFileTreeView() {
		this.fileTreeView = new FileTreeView();
		
		VerticalPanel verticalPanel = new VerticalPanel();
		
		//StatusWidget statusWidget = new StatusWidget();
		this.uploader = new MyUploader();
		//uploader.setStatusWidget(statusWidget);
		uploader.addButton(new Button("Add file"));
		uploader.removeStyleName("gwt-Fileupload");
		IUploadStatus statusWidget = uploader.getStatusWidget();
		//uploader.addStyleName("gwt-Button");
		//uploader.getFileInput().getWidget().addStyleName("gwt-Button");
		//for(IUploader uploader : multiUploader.getUploaders()) 
		//	System.out.println(uploader.getClass());
		//System.out.println(multiUploader.getUploaders());

		this.deleteButton = new Button("Delete");
		this.renameButton = new Button("Rename");
		this.createDirectoryButton = new Button("Create Folder");

		verticalPanel.add(new Label("Your Files:"));
		verticalPanel.add(new ScrollPanel(fileTreeView));
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		horizontalPanel.add(createDirectoryButton);
		horizontalPanel.add(renameButton);
		horizontalPanel.add(deleteButton);
		horizontalPanel.add(uploader);
		
		verticalPanel.add(horizontalPanel);
		verticalPanel.add(statusWidget.getWidget());
	
		this.initWidget(verticalPanel);
	}
	
	
	@Override
	public FileTreeView getFileTreeView() {
		return fileTreeView;
	}


	@Override
	public Uploader getUploader() {
		return this.uploader;
	}


	@Override
	public Button getCreateDirectoryButton() {
		return this.createDirectoryButton;
	}


	@Override
	public Button getRenameButton() {
		return this.renameButton;
	}


	@Override
	public Button getDeleteButton() {
		return this.deleteButton;
	}
}
