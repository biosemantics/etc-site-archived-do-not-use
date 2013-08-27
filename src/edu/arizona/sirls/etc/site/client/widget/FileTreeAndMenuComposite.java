package edu.arizona.sirls.etc.site.client.widget;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.builder.dialog.LoadingPopup;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.CreateDirectoryClickHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.DeleteClickHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.MyUploader;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.MyUploaderConstants;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.OnFinishUploadHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.OnStartUploadHandler;
import edu.arizona.sirls.etc.site.client.builder.lib.fileManager.RenameClickHandler;
import gwtupload.client.IUploadStatus;

public class FileTreeAndMenuComposite extends Composite implements ILoadListener {

	private FileTreeComposite fileTree;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private VerticalPanel verticalPanel = new VerticalPanel();
	
	public FileTreeAndMenuComposite(boolean enableDragAndDrop) { 
		loadingPopup.center(); 
		loadingPopup.show(); 
		
		this.fileTree = new FileTreeComposite(enableDragAndDrop);
		fileTree.addLoadListener(this);
		fileTree.refresh();
		
		initWidget(verticalPanel);
	}

	@Override
	public void notifyLoadFinished(Widget widget) { 
		loadingPopup.hide();
		
		//StatusWidget statusWidget = new StatusWidget();
		MyUploader uploader = new MyUploader();
		uploader.setAutoSubmit(true);
		uploader.setI18Constants(new MyUploaderConstants());
		//uploader.setStatusWidget(statusWidget);
		uploader.addButton(new Button("Add file"));
		uploader.removeStyleName("gwt-Fileupload");
		IUploadStatus statusWidget = uploader.getStatusWidget();
		//uploader.addStyleName("gwt-Button");
		//uploader.getFileInput().getWidget().addStyleName("gwt-Button");
		//for(IUploader uploader : multiUploader.getUploaders()) 
		//	System.out.println(uploader.getClass());
		//System.out.println(multiUploader.getUploaders());
		uploader.setServletPath(uploader.getServletPath() + "?username=" + Authentication.getInstance().getUsername()
				+ "&sessionID=" + Authentication.getInstance().getSessionID());
		uploader.addOnFinishUploadHandler(new OnFinishUploadHandler(fileTree, uploader.getServletPath()));
		OnStartUploadHandler onStartUploadHandler = new OnStartUploadHandler(fileTree.getFileSelectionHandler());
		uploader.addOnStartUploadHandler(onStartUploadHandler);
		uploader.setTitle("Add/Upload a File");
		Button deleteButton = new Button("Delete");
		Button renameButton = new Button("Rename");
		Button createDirectoryButton = new Button("Create Folder");
		deleteButton.addClickHandler(new DeleteClickHandler(fileTree.getFileSelectionHandler(), fileTree));
		renameButton.addClickHandler(new RenameClickHandler(fileTree.getFileSelectionHandler(), fileTree));
		createDirectoryButton.addClickHandler(new CreateDirectoryClickHandler(fileTree.getFileSelectionHandler(), fileTree));

		verticalPanel.add(new Label("Your Files:"));
		verticalPanel.add(new ScrollPanel(fileTree));
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		horizontalPanel.add(createDirectoryButton);
		horizontalPanel.add(renameButton);
		horizontalPanel.add(deleteButton);
		horizontalPanel.add(uploader);
		
		
		verticalPanel.add(horizontalPanel);
		verticalPanel.add(statusWidget.getWidget());
	}
	
}
