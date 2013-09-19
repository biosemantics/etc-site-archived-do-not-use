package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.presenter.ILabelTextFieldDialogBoxHandler;
import edu.arizona.sirls.etc.site.client.presenter.LabelTextFieldPresenter;
import edu.arizona.sirls.etc.site.client.presenter.MyUploaderConstants;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.presenter.LabelTextFieldPresenter.Display;
import edu.arizona.sirls.etc.site.client.view.LabelTextFieldView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;
import gwtupload.client.Uploader;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class ManagableFileTreePresenter implements Presenter {

	public interface Display {
		Widget asWidget();
		Uploader getUploader();
		Button getCreateDirectoryButton();
		Button getRenameButton();
		Button getDeleteButton();
		FileTreePresenter.Display getFileTreeView();
	}

	private Display display;
	private HandlerManager eventBus;
	private IFileServiceAsync fileService;
	private FileTreePresenter.Display fileTreeView;
	private FileTreePresenter fileTreePresenter;
	private FileSelectionHandler fileSelectionHandler;
	private String defaultServletPath;
	
	public ManagableFileTreePresenter(HandlerManager eventBus, Display display, 
			IFileServiceAsync fileService, boolean enableDragAndDrop, FileFilter fileFilter) { 
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
		this.fileTreeView = display.getFileTreeView();
		this.fileTreePresenter = new FileTreePresenter(eventBus, 
				fileTreeView, fileService, enableDragAndDrop, fileFilter);
		this.fileSelectionHandler = fileTreePresenter.getFileSelectionHandler();
		bind();
	}

	private void bind() {
		display.getDeleteButton().addClickHandler(new DeleteClickHandler());
		display.getRenameButton().addClickHandler(new RenameClickHandler());
		display.getCreateDirectoryButton().addClickHandler(new CreateDirectoryClickHandler());
		
		display.getUploader().addOnFinishUploadHandler(new OnFinishUploadHandler());
		display.getUploader().setServletPath(display.getUploader().getServletPath() + "?username=" + Authentication.getInstance().getUsername()
				+ "&sessionID=" + Authentication.getInstance().getSessionID());
		this.defaultServletPath = display.getUploader().getServletPath();
		display.getUploader().addOnStartUploadHandler(new OnStartUploadHandler());
		display.getUploader().setTitle("Add/Upload a File");
		display.getUploader().setAutoSubmit(true);
		display.getUploader().setI18Constants(new MyUploaderConstants());
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	protected class DeleteClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			String target = fileSelectionHandler.getTarget();
			if(target != null && !target.isEmpty()) {
				fileService.deleteFile(Authentication.getInstance().getAuthenticationToken(), target, new AsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(Boolean result) {
						fileTreePresenter.refresh();
					}
				});
			}
		}
	}
	
	
	protected class RenameClickHandler implements ClickHandler, ILabelTextFieldDialogBoxHandler {
		@Override
		public void onClick(ClickEvent event) {
			String target = fileSelectionHandler.getTarget();
			//don't allow rename of root node
			if(target != null && !target.isEmpty()) {
				String pathParts[] = target.split("//");
				LabelTextFieldView renameView = new LabelTextFieldView();
				LabelTextFieldPresenter renameDialogBox = new LabelTextFieldPresenter(
						renameView, "Rename", "New name: ", pathParts[pathParts.length-1], this);
				renameDialogBox.go();
			}
		}

		private String getTargetFromParts(String[] parts) {
			StringBuilder builder = new StringBuilder();
			for(String part : parts) {
			    if(builder.length() != 0)
			    	builder.append("//");
				builder.append(part);
			}
			return builder.toString();
		}

		@Override
		public void canceled() {
			
		}

		@Override
		public void confirmed(String newFileName) {
			String target = fileSelectionHandler.getTarget();
			if(target != null) {
				String pathParts[] = target.split("//");
				pathParts[pathParts.length-1] = newFileName;
				String newTarget = getTargetFromParts(pathParts);
				fileService.moveFile(Authentication.getInstance().getAuthenticationToken(), target, newTarget, 
						new AsyncCallback<Boolean>() {
					public void onSuccess(Boolean result) {
						if(result) {
							fileTreePresenter.refresh();
						}		
					}
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
			}
		}
	}
	
	protected class CreateDirectoryClickHandler implements ClickHandler, ILabelTextFieldDialogBoxHandler {
		@Override
		public void onClick(ClickEvent event) {
			LabelTextFieldView renameView = new LabelTextFieldView();
			LabelTextFieldPresenter renamePresenter = new LabelTextFieldPresenter(
					renameView, "Create folder", "Folder name:", "", this);
			renamePresenter.go();
		}
		
		@Override
		public void canceled() {
		}

		@Override
		public void confirmed(String directoryName) {
			String target = fileSelectionHandler.getTarget();
			if(target != null) {
				fileService.createDirectory(Authentication.getInstance().getAuthenticationToken(), target, directoryName, 
						new AsyncCallback<Boolean>() {
					public void onSuccess(Boolean result) {
						if (result) {
							fileTreePresenter.refresh();
						}
					}
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
			}
		}
	}
	
	public class OnFinishUploadHandler implements OnFinishUploaderHandler {
		@Override
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				fileTreePresenter.refresh();
			}
			uploader.setServletPath(defaultServletPath);
		}
	}
	
	public class OnStartUploadHandler implements OnStartUploaderHandler {
		@Override
		public void onStart(IUploader uploader) {
			String target = fileSelectionHandler.getTarget();
			if(target != null)
				uploader.setServletPath(uploader.getServletPath() + "&target=" + target);
		}
	}
}
