package edu.arizona.sirls.etc.site.client.presenter.fileManager;

import java.util.List;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Configuration;
import edu.arizona.sirls.etc.site.client.presenter.ILabelTextFieldDialogBoxHandler;
import edu.arizona.sirls.etc.site.client.presenter.LabelTextFieldCancelConfirmPresenter;
import edu.arizona.sirls.etc.site.client.presenter.LabelTextFieldConfirmPresenter;
import edu.arizona.sirls.etc.site.client.presenter.MessagePresenter;
import edu.arizona.sirls.etc.site.client.presenter.MyUploaderConstants;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.LabelTextFieldCancelConfirmView;
import edu.arizona.sirls.etc.site.client.view.LabelTextFieldConfirmView;
import edu.arizona.sirls.etc.site.client.view.MessageView;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileTypeEnum;

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
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class ManagableFileTreePresenter implements Presenter {

	public interface Display {
		Widget asWidget();
		IUploader getUploader();
		Button getCreateDirectoryButton();
		Button getRenameButton();
		Button getDeleteButton();
		FileTreeView getFileTreeView();
		void setStatusWidget(Widget statusWidget);
		Button getAddButton();
		Button getDownloadButton();
	}

	private Display display;
	private HandlerManager eventBus;
	private IFileServiceAsync fileService;
	private FileTreeView fileTreeView;
	private FileTreePresenter fileTreePresenter;
	private FileSelectionHandler fileSelectionHandler;
	private String defaultServletPath;
	private MessageView messageView = new MessageView();
	private MessagePresenter messagePresenter = new MessagePresenter(messageView, "File Selection");
	
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
	    
	    display.getAddButton().addClickHandler(new AddClickHandler());
		display.getUploader().setFileInput(new MyFileInput(display.getAddButton()));
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}
	
	protected class AddClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			final FileImageLabelTreeItem selection = fileSelectionHandler.getSelection();
			if(selection == null) {
				display.getUploader().setEnabled(false);
				messagePresenter.setMessage("Please select a valid directory to add the files to");
				messagePresenter.go();
			} else if(selection.getFileInfo().getFilePath() == null) {
				messagePresenter.setMessage("Please select a valid directory to add the files to");
				messagePresenter.go();
			} else {
				display.getUploader().setEnabled(true);
			}
		}
	}

	protected class DownloadClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			final FileImageLabelTreeItem selection = fileSelectionHandler.getSelection();
			if(selection != null) { 
				final String selectionPath = selection.getFileInfo().getFilePath();
				if(selectionPath != null) {
					fileService.isDirectory(Authentication.getInstance().getAuthenticationToken(), selectionPath, new AsyncCallback<RPCResult<Boolean>>() {
						@Override
						public void onFailure(Throwable caught) {
						
						}
						@Override
						public void onSuccess(RPCResult<Boolean> result) {
							if(result.isSucceeded()) {
								if(result.getData()) {
									fileService.zipDirectory(Authentication.getInstance().getAuthenticationToken(), selectionPath, new AsyncCallback<RPCResult<Void>>() {
										@Override
										public void onFailure(Throwable caught) {
											caught.printStackTrace();
										}
										@Override
										public void onSuccess(RPCResult<Void> result) {
											Window.open("/etcsite/download/?target=" + selectionPath + "&directory=yes&username=" + Authentication.getInstance().getUsername() + "&" + 
													"sessionID=" + Authentication.getInstance().getSessionID()
													, "download", "resizable=yes,scrollbars=yes,menubar=yes,location=yes,status=yes");
										}
									});
								} else {
									Window.open("/etcsite/download/?target=" + selectionPath + "&username=" + Authentication.getInstance().getUsername() + "&" + 
											"sessionID=" + Authentication.getInstance().getSessionID()
											, "download", "resizable=yes,scrollbars=yes,menubar=yes,location=yes,status=yes");
								}
							}
						} 
					});
				} else {
					messagePresenter.setMessage("Not downloadable");
					messagePresenter.go();
				}
			} else {
				messagePresenter.setMessage("Please select a file to download");
				messagePresenter.go();
			}
		} 
	}
	
	protected class DeleteClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			FileImageLabelTreeItem selection = fileSelectionHandler.getSelection();
			
			if(selection != null && selection.getFileInfo().getFilePath() != null) {
				fileService.deleteFile(Authentication.getInstance().getAuthenticationToken(), selection.getFileInfo().getFilePath(), new AsyncCallback<RPCResult<Void>>(){
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(RPCResult<Void> result) {
						fileSelectionHandler.clear();
						fileTreePresenter.refresh();
					}
				});
			} else {
				messagePresenter.setMessage("Please select a file or directory to delete");
				messagePresenter.go();
			}
		}
	}
	
	
	protected class RenameClickHandler implements ClickHandler, ILabelTextFieldDialogBoxHandler {
		@Override
		public void onClick(ClickEvent event) {
			FileImageLabelTreeItem selection = fileSelectionHandler.getSelection();
			//don't allow rename of root node
			if(selection != null) {
				LabelTextFieldCancelConfirmView renameView = new LabelTextFieldCancelConfirmView();
				LabelTextFieldCancelConfirmPresenter renameDialogBox = new LabelTextFieldCancelConfirmPresenter(
						renameView, "Rename", "New name: ", selection.getFileInfo().getName(), this);
				renameDialogBox.go();
			} else {
				messagePresenter.setMessage("Please select a file or directory to rename");
				messagePresenter.go();
			}
		}

		@Override
		public void confirmed(final String newFileName) {
			final FileImageLabelTreeItem selection = fileSelectionHandler.getSelection();
			if(selection != null && selection.getFileInfo().getFilePath() != null) {
				fileService.renameFile(Authentication.getInstance().getAuthenticationToken(), selection.getFileInfo().getFilePath(), newFileName, 
						new AsyncCallback<RPCResult<Void>>() {
					public void onSuccess(RPCResult<Void> result) {
						if(result.isSucceeded()) {
							//fileSelectionHandler..setTarget(newTarget);
							fileTreePresenter.refresh();
						} else {
							messagePresenter.setMessage("File could not be renamed.");
							messagePresenter.go();
						}
						selection.getFileInfo().setName(newFileName);
					}
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
			}
		}

		@Override
		public void canceled() {
			// TODO Auto-generated method stub
			
		}
	}
	
	protected class CreateDirectoryClickHandler implements ClickHandler, ILabelTextFieldDialogBoxHandler {
		@Override
		public void onClick(ClickEvent event) {
			final FileImageLabelTreeItem selection = fileSelectionHandler.getSelection();
			if(selection != null) {
				int level = getLevel(selection);
				if(level < Configuration.fileManagerMaxDepth) {
					LabelTextFieldConfirmView renameView = new LabelTextFieldConfirmView();
					LabelTextFieldConfirmPresenter renamePresenter = new LabelTextFieldConfirmPresenter(
							renameView, "Create folder", "Folder name:", "", this);
					renamePresenter.go();
				} else {
					messagePresenter.setMessage("Only a directory depth of " + Configuration.fileManagerMaxDepth + " is allowed.");
					messagePresenter.go();
				}
			} else {
				messagePresenter.setMessage("Please select a parent directory.");
				messagePresenter.go();
			}
		}
		
		private int getLevel(TreeItem item) {
			int result = 0;
			while(item.getParentItem() != null) {
				result++;
				item = item.getParentItem();
			}
			return result;
		}

		@Override
		public void canceled() {
		}

		@Override
		public void confirmed(final String directoryName) {
			final FileImageLabelTreeItem selection = fileSelectionHandler.getSelection();
			final String selectionPath = selection.getFileInfo().getFilePath();
			fileService.isDirectory(Authentication.getInstance().getAuthenticationToken(), selectionPath, new AsyncCallback<RPCResult<Boolean>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(RPCResult<Boolean> result) {
					String newDirectoryParent = selectionPath;
					if(result.isSucceeded()) {
						if(!result.getData())
							newDirectoryParent = getParent(selection);
						if(newDirectoryParent != null) {
							fileService.createDirectory(Authentication.getInstance().getAuthenticationToken(), newDirectoryParent, directoryName, 
									new AsyncCallback<RPCResult<Void>>() {
								public void onSuccess(RPCResult<Void> result) {
									if (result.isSucceeded()) {
										fileTreePresenter.refresh();
									} else {
										messagePresenter.setMessage("Could not create directory.");
										messagePresenter.go();
									}
								}
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
								}
							});
						}
					}
				}
			});
		}
	}
	
	private String getParent(FileImageLabelTreeItem selection) {
		TreeItem parentItem = selection.getParentItem();
		if(parentItem != null && parentItem instanceof FileImageLabelTreeItem) {
			FileImageLabelTreeItem parentFileTreeItem = (FileImageLabelTreeItem)parentItem;
			if(parentFileTreeItem.getFileInfo().getFilePath() == null) {
				return this.getParent(parentFileTreeItem);
			}
			return parentFileTreeItem.getFileInfo().getFilePath();
		}
		return null;
	}
	
	public class OnFinishUploadHandler implements OnFinishUploaderHandler {
		@Override
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				fileTreePresenter.refresh();
			}
			
			String serverResponse = uploader.getServerInfo().message;
			if(serverResponse != null && !serverResponse.isEmpty()) {
				serverResponse = serverResponse.replaceAll("\n", "<br>");
				messagePresenter.setMessage(serverResponse);
				messagePresenter.go();
			}
			//only needed when MultiUploader is used instead of SingleUploader. There somewhat of a new instance of MultiUploader
			//is created for each additional upload.
			//try to avoid MultiUploader, it will create a new HTML element for each uploader making it complex to style
			//also MultiUploader is not needed, in comparison to SingleUploader it only allows to append additional uploads once
			//a previous upload is still running. It doesn't mean multiple files, this can also be done with SingleUploader
			/*IFileInput ctrl = display.getUploader().getFileInput();
		    DOM.setElementProperty(((UIObject) ctrl).getElement(), "multiple", "multiple");*/
			
			uploader.setServletPath(defaultServletPath);
			enableManagement();
		}
	}
	
	public class OnStartUploadHandler implements OnStartUploaderHandler {
		@Override
		public void onStart(final IUploader uploader) {
			final FileImageLabelTreeItem selection = fileSelectionHandler.getSelection();
			if(fileSelectionHandler.getSelection().getFileInfo().getFileType().equals(FileTypeEnum.DIRECTORY)) {
				uploader.setServletPath(uploader.getServletPath() + "&target=" + selection.getFileInfo().getFilePath());
			} else {
				String newFilePath = getParent(selection);
				uploader.setServletPath(uploader.getServletPath() + "&target=" + newFilePath);
			}				

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
	
	public void refresh() {
		this.fileTreePresenter.refresh();
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
