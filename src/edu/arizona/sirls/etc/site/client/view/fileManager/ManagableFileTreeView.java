package edu.arizona.sirls.etc.site.client.view.fileManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.presenter.fileManager.ManagableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.MyFileInput;
import gwtupload.client.IFileInput;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploader;
import gwtupload.client.MultiUploader;
import gwtupload.client.SingleUploader;
import gwtupload.client.IFileInput.ButtonFileInput;

//can't subclass FileTreeView sa initWidget may only be called once
public class ManagableFileTreeView extends Composite implements ManagableFileTreePresenter.Display{
	
	private FileTreeView fileTreeView;
	private SingleUploader uploader;
	private Button deleteButton;
	private Button renameButton;
	private Button createDirectoryButton;
	private SimplePanel statusWidgetPanel = new SimplePanel();
	private Button addButton;
	private Button downloadButton;

	public ManagableFileTreeView() {
		this.fileTreeView = new FileTreeView();
		
		VerticalPanel verticalPanel = new VerticalPanel();
		
		//StatusWidget statusWidget = new StatusWidget();
		
		//this.uploader = new WorkaroundInput("Add files"); 
		this.uploader = new SingleUploader();
		this.addButton = new Button();
		
		//uploader.setStatusWidget(statusWidget);
	    //Button addButton = new Button("Add file");
	    //DOM.setElementProperty(addButton.getElement(), "multiple", "multiple");
		//uploader.addButton(addButton);
		
		//uploader.removeStyleName("gwt-Fileupload");
		//uploader.addStyleName("gwt-Button");
		//uploader.getFileInput().getWidget().addStyleName("gwt-Button");
		//for(IUploader uploader : multiUploader.getUploaders()) 
		//	System.out.println(uploader.getClass());
		//System.out.println(multiUploader.getUploaders());

		this.downloadButton = new Button("Download");
		this.deleteButton = new Button("Delete");
		this.renameButton = new Button("Rename");
		this.createDirectoryButton = new Button("Create Folder");

		verticalPanel.add(new Label("Your Files:"));
		verticalPanel.add(new ScrollPanel(fileTreeView));
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		horizontalPanel.add(createDirectoryButton);
		horizontalPanel.add(renameButton);
		horizontalPanel.add(deleteButton);
		horizontalPanel.add(downloadButton);
		horizontalPanel.add(uploader);
		
		verticalPanel.add(horizontalPanel);
		statusWidgetPanel.setStyleName("GWTUpld");
		verticalPanel.add(statusWidgetPanel);
	
		this.initWidget(verticalPanel);
	}
	
	
	@Override
	public FileTreeView getFileTreeView() {
		return fileTreeView;
	}


	@Override
	public IUploader getUploader() {
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
	
	public void setStatusWidget(Widget statusWidget) {
		this.statusWidgetPanel.setWidget(statusWidget);
	}
	
	public class WorkaroundInput extends Composite {
		
		private SingleUploader uploader = new SingleUploader();
		private Button showingButton = new Button();
		
		public WorkaroundInput(String name) {
			showingButton.setText(name);
			uploader.sinkEvents(Event.ONCLICK);
			uploader.getFileInput().getWidget().setStyleName("this-is-a-test-style");
			uploader.setFileInput(new IFileInput.ButtonFileInput());
			
			uploader.addHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						//won't work, probably not even possible at all
						//showingButton.setStyleName("gwt-Button:active");
						showingButton.click(); //make the click visible on the fake button
					} 
				}, ClickEvent.getType());
			FlowPanel panel = new FlowPanel();
			panel.add(uploader);
			panel.setStyleName("fileinputs");
			FlowPanel secondPanel = new FlowPanel();
			secondPanel.setStyleName("fakefile");
			secondPanel.add(showingButton);
			panel.add(secondPanel);
			this.initWidget(panel);
		}

		public IUploadStatus getStatusWidget() {
			return uploader.getStatusWidget();
		}
		
		public IUploader getUploader() {
			return this.uploader;
		}
	}

	@Override
	public Button getAddButton() {
		return this.addButton;
	}


	@Override
	public Button getDownloadButton() {
		return this.downloadButton;
	}
	
	
}
