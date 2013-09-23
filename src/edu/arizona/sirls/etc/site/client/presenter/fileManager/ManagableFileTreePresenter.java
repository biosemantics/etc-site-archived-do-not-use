package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.presenter.ILabelTextFieldDialogBoxHandler;
import edu.arizona.sirls.etc.site.client.presenter.LabelTextFieldPresenter;
import edu.arizona.sirls.etc.site.client.presenter.MyUploaderConstants;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.LabelTextFieldView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IFileInput;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class ManagableFileTreePresenter implements Presenter {

	public interface Display {
		Widget asWidget();
		IUploader getUploader();
		Button getCreateDirectoryButton();
		Button getRenameButton();
		Button getDeleteButton();
		FileTreePresenter.Display getFileTreeView();
		void setStatusWidget(Widget statusWidget);
		Button getAddButton();
		Button getDownloadButton();
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
		display.getUploader().addOnStartUploadHandler(new OnStartUploadHandler());
		display.getUploader().setServletPath(display.getUploader().getServletPath() + "?username=" + Authentication.getInstance().getUsername()
				+ "&sessionID=" + Authentication.getInstance().getSessionID());
		this.defaultServletPath = display.getUploader().getServletPath();
		display.getUploader().setI18Constants(new MyUploaderConstants());
		display.getUploader().setAutoSubmit(true);
	    display.setStatusWidget(display.getUploader().getStatusWidget().getWidget());
	    
	    IUploadStatus statusWidget = new BaseUploadStatus();
	    statusWidget.setCancelConfiguration(IUploadStatus.DEFAULT_CANCEL_CFG);
	    display.getUploader().setStatusWidget(statusWidget);
	    display.setStatusWidget(statusWidget.getWidget());
	    
	    display.getDownloadButton().addClickHandler(new DownloadClickHandler());
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

	protected class DownloadClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			String target = fileSelectionHandler.getTarget();
			if(target != null && !target.isEmpty()) {
				
				Window.open("/etcsite/download/?target=" + target + "&username=" + Authentication.getInstance().getUsername() + "&" + 
						"sessionID=" + Authentication.getInstance().getSessionID()
						, "download", "resizable=yes,scrollbars=yes,menubar=yes,location=yes,status=yes");
			}
		} 
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
			
			//only needed when MultiUploader is used instead of SingleUploader. There somewhat of a new instance of MultiUploader
			//is created for each additional upload.
			//try to avoid MultiUploader, it will create a new HTML element for each uploader making it complex to style
			//also MultiUploader is not needed, in comparison to SingleUploader it only allows to append additional uploads once
			//a previous upload is still running. It doesn't mean multiple files, this can also be done with SingleUploader
			/*IFileInput ctrl = display.getUploader().getFileInput();
		    DOM.setElementProperty(((UIObject) ctrl).getElement(), "multiple", "multiple");
		    
		    uploader.setServletPath(defaultServletPath);
		    uploader.setAutoSubmit(true); */
			
			enableManagement();
		}
	}
	
	public class OnStartUploadHandler implements OnStartUploaderHandler {
		@Override
		public void onStart(IUploader uploader) {
			String target = fileSelectionHandler.getTarget();
			if(target != null)
				uploader.setServletPath(uploader.getServletPath() + "&target=" + target);
			//only needed when MultiUploader is used instead of SingleUploader
			//display.setStatusWidget(display.getUploader().getStatusWidget().getWidget());
			
			/*
			 * Creation of directories directly inside of the upload target should not be possible (possible name clash)
			 * Rename of target and files directly inside of target should not be possible (target no longer available, name clash)
			 * Delete of target should not be possible (target no longer available)
			 */
			
			// for now, just disable all of the others:
			disableManagement();
		}
	}
	
	private void disableManagement() {
		display.getCreateDirectoryButton().setEnabled(false);
		display.getRenameButton().setEnabled(false);
		display.getDeleteButton().setEnabled(false);
		display.getUploader().getFileInput().getWidget().getElement().removeAttribute("aria-hidden");
		display.getAddButton().getElement().removeAttribute("aria-hidden");
		display.getAddButton().setEnabled(false);
	}
	
	private void enableManagement() {
		display.getCreateDirectoryButton().setEnabled(true);
		display.getRenameButton().setEnabled(true);
		display.getDeleteButton().setEnabled(true);
		display.getAddButton().setEnabled(true);
	}
	
	
	/**
	 * There are only workarounds known to style the button inside of a input file element.
	 * To overcome the problem:
	 * a) http://www.quirksmode.org/dom/inputfile.html can be used, however with this solution the click on the fake button is style-wise
	 * not reflected. Also it is somewhat of an ugly solution, requiring to change e.g. css whenever the text inside the fake button becomes
	 * wider.
	 * b) IFileInput.ButtonFileInput can be used rather than BrowserFileInput, which takes care of using the correct style (also on-click).
	 * However, with this solution the multiple attribute cannot be set using (1), because the FileInput is the ButtonFileInput (which 
	 * corresponds to a button element in html) rather than the BrowserFileInput (which corresponds to the input 
	 * file html element that actually requires the multiple attribute). (1) can be used going with solution a).
	 * 
	 * (1):
	 * IFileInput ctrl = uploader.getFileInput();
   	 * DOM.setElementProperty(((UIObject) ctrl).getElement(), "multiple", "multiple");
   	 * 
   	 * To set the multiple attribute on the input file element this javascript implementation was created, and to be able to go with b)
	 */
	public static native void setInputFileMultiple() /*-{
		var inputs, index;
		inputs = $doc.getElementsByTagName('input');	
		for (index = 0; index < inputs.length; ++index) {
			if(inputs[index].getAttribute("type") == "file" && inputs[index].getAttribute("class") == "gwt-FileUpload") {
				inputs[index].setAttribute("multiple", "multiple");
			}
		}
	}-*/;

}
