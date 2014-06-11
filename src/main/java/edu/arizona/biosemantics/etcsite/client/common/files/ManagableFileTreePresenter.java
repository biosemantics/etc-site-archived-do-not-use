
package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.Configuration;
import edu.arizona.biosemantics.etcsite.client.common.IMessageView;
import edu.arizona.biosemantics.etcsite.client.common.ITextInputView;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.client.common.ITextInputView.ITextInputListener;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.client.common.MessageView;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesDialogPresenter.ICloseHandler;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.IFileTreeSelectionListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;
import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput;
import gwtupload.client.IFileInput.BrowserFileInput;
import gwtupload.client.IFileInput.ButtonFileInput;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;

public class ManagableFileTreePresenter implements IManagableFileTreeView.Presenter, IFileTreeSelectionListener {

	private IFileServiceAsync fileService;
	private IManagableFileTreeView view;
	private IFileTreeView.Presenter fileTreePresenter;
	private FileFilter fileFilter;
	private IMessageView.Presenter messagePresenter;
	private String defaultServletPath;
	private ITextInputView.Presenter textInputPresenter;
	private ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter;
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	@Inject
	public ManagableFileTreePresenter(IManagableFileTreeView view, 
			IFileTreeView.Presenter fileTreePresenter, 
			IFileServiceAsync fileService, MessageView.Presenter messagePresenter, 
			ITextInputView.Presenter textInputPresenter, 
			ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter) {
		this.fileService = fileService;
		this.view = view;
		this.view.setPresenter(this);
		this.fileTreePresenter = fileTreePresenter;
		fileTreePresenter.addSelectionListener(this);
		this.messagePresenter = messagePresenter;
		this.textInputPresenter = textInputPresenter;
		this.createSemanticMarkupFilesDialogPresenter = createSemanticMarkupFilesDialogPresenter;
		
		defaultServletPath = view.getUploader().getServletPath();
		view.getUploader().setServletPath(defaultServletPath);
		view.getUploader().addOnFinishUploadHandler(new OnFinishUploadHandler());
		view.getUploader().addOnStartUploadHandler(new OnStartUploadHandler());
		view.getUploader().setI18Constants(new MyUploaderConstants());

		IUploadStatus statusWidget = new BaseUploadStatus();
	    statusWidget.setCancelConfiguration(IUploadStatus.DEFAULT_CANCEL_CFG);
	    //show uploading constantly, instead of the files
	    /*display.getUploader().addOnStatusChangedHandler(new OnStatusChangedHandler() {
			@Override
			public void onStatusChanged(IUploader uploader) {
				uploader.getStatusWidget().setFileName("Uploading...");
			}
	    });
	    display.getUploader().addOnChangeUploadHandler(new OnChangeUploaderHandler() {
			@Override
			public void onChange(IUploader uploader) {
				System.out.println("on change");
			}
	    });*/
	    view.getUploader().setStatusWidget(statusWidget);
	    view.setStatusWidget(statusWidget.getWidget());
		view.getUploader().setFileInput(new MyFileInput(view.getAddButton()));
	
		initActions();
	}
	
	@Override
	public void refresh(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
		fileTreePresenter.refresh(fileFilter);
		//setInputFileMultiple();
	}
	
	@Override
	public void onCreateSemanticMarkupFiles() {
		final FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
		if(selection != null && selection.getFileInfo().isAllowsNewChildren()) {
			createSemanticMarkupFilesDialogPresenter.setCloseHandler(new ICloseHandler() {
				@Override
				public void onClose(int filesCreated) {
					if(filesCreated > 0)
						refresh(fileFilter);
				}
			});
			createSemanticMarkupFilesDialogPresenter.show(fileTreePresenter.getSelectedItem().getFileInfo().getFilePath());
		}
		else
			messagePresenter.showMessage("No destination selected", "Please select a valid parent directory");
	}
	
	@Override
	public void onCreate() {
		final FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
		if(selection != null && selection.getFileInfo().isAllowsNewChildren()) {
			int level = getLevel(selection);
			if(level < Configuration.fileManagerMaxDepth) {
				textInputPresenter.show("Create folder", "Folder name:", "", new ITextInputListener() {
					@Override
					public void onConfirm(final String directoryName) {
						final FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
						final String selectionPath = selection.getFileInfo().getFilePath();
						fileService.isDirectory(Authentication.getInstance().getToken(), selectionPath, new RPCCallback<Boolean>() {
							@Override
							public void onResult(Boolean result) {
								String newDirectoryParent = selectionPath;
								if(!result)
									newDirectoryParent = getParent(selection);
								if(newDirectoryParent != null) {
									fileService.createDirectory(Authentication.getInstance().getToken(), newDirectoryParent, directoryName, false,
											new RPCCallback<String>() {
												@Override
												public void onResult(String result) {
													fileTreePresenter.refresh(fileFilter);
												}
	
									});
								}
							}
						});
					}
					@Override
					public void onCancel() { }
				});
			} else {
				messagePresenter.showMessage("File Manager", "Only a directory depth of " + Configuration.fileManagerMaxDepth + " is allowed.");
			}
		} else {
			messagePresenter.showMessage("File Manager", "Please select a valid parent directory.");
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
	public void onRename() {
		FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
		//don't allow rename of root node
		if(selection != null && !selection.getFileInfo().isSystemFile()) {
			textInputPresenter.show("Rename", "New name", selection.getFileInfo().getName(), new ITextInputListener() {
				@Override
				public void onConfirm(final String newFileName) {
					final FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
					if(selection != null && !selection.getFileInfo().isSystemFile()) {
						fileService.renameFile(Authentication.getInstance().getToken(), selection.getFileInfo().getFilePath(), newFileName, 
								new RPCCallback<Void>() {
							@Override
							public void onResult(Void result) {
								fileTreePresenter.refresh(fileFilter);
								selection.getFileInfo().setName(newFileName);
							}
						});
					}
				}
				@Override
				public void onCancel() {}
			});
		} else {
			messagePresenter.showMessage("File Manager", "Please select a valid file or directory to rename");
		}
	}
	

	@Override
	public void onDelete() {
		FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
		if(selection != null && !selection.getFileInfo().isSystemFile()) {
			fileService.deleteFile(Authentication.getInstance().getToken(), selection.getFileInfo().getFilePath(), new RPCCallback<Void>(){
				@Override
				public void onResult(Void result) {
					ManagableFileTreePresenter.this.initActions();
					fileTreePresenter.clearSelection();
					fileTreePresenter.refresh(fileFilter);
				}
				
			});
		} else {
			messagePresenter.showMessage("File Manager", "Please select a valid file or directory to delete");
		}
	}

	@Override
	public void onDownload() {
		final FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
		if(selection != null) { 
			final String selectionPath = selection.getFileInfo().getFilePath();
			if(selectionPath != null) {
				loadingPopup.start();
				fileService.getDownloadPath(Authentication.getInstance().getToken(), selectionPath, new RPCCallback<String>() {
					@Override
					public void onResult(String result) {
						//target=" + result.getData() + "&directory=yes
						loadingPopup.stop();
						Window.Location.replace("/etcsite/download/?target=" + URL.encodeQueryString(result) + 
								"&username=" + URL.encodeQueryString(Authentication.getInstance().getUsername()) + "&" + 
								"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionID()));
						
						/*Window.open("/etcsite/download/?target=" + result.getData() + "&username=" + Authentication.getInstance().getUsername() + "&" + 
								"sessionID=" + Authentication.getInstance().getSessionID()
								, "download", "resizable=yes,scrollbars=yes,menubar=yes,location=yes,status=yes"); */
					}
				});
			} else {
				messagePresenter.showMessage("File Manager", "Not downloadable");
			}
		} else {
			messagePresenter.showMessage("File Manager", "Please select a file to download");
		}
	}
	
	/**
	 * multiple file uploading is enabled by using the SingleUploader and the native method setInputFileMultiple() called in FileTreePresenter.java
	 * @author updates
	 *
	 */
	public class OnFinishUploadHandler implements OnFinishUploaderHandler {
		@Override
		public void onFinish(IUploader uploader) {	
			
			String serverResponse = uploader.getServerInfo().message;
			if(serverResponse != null && !serverResponse.isEmpty()) {
				serverResponse = serverResponse.replaceAll("\n", "<br>");
				messagePresenter.showMessage("File Manager", serverResponse);
			}
			
			if (uploader.getStatus() == Status.SUCCESS) {
				List<String> fileNames = new LinkedList<String>();
				fileNames.add("Done uploading.");
				uploader.getStatusWidget().setFileNames(fileNames);
				//uploader.getStatusWidget().setFileName("Done uploading.");
				fileTreePresenter.refresh(fileFilter);
			}
						
			uploader.setServletPath(defaultServletPath);
			enableManagement();
		}
	}

	public class OnStartUploadHandler implements OnStartUploaderHandler {
		@Override
		public void onStart(final IUploader uploader) {			
			String servletPath = view.getUploader().getServletPath() + "?username=" + URL.encodeQueryString(Authentication.getInstance().getUsername())
					+ "&sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionID());
			uploader.setServletPath(servletPath);
			
			List<String> fileNames = new LinkedList<String>();
			fileNames.add("Uploading, please wait...");
			uploader.getStatusWidget().setFileNames(fileNames);
			final FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
			if(selection.getFileInfo().getFileType().equals(FileTypeEnum.DIRECTORY)) {
				uploader.setServletPath(uploader.getServletPath() + "&target=" + URL.encodeQueryString(selection.getFileInfo().getFilePath()));
			} else {
				String newFilePath = getParent(selection);
				uploader.setServletPath(uploader.getServletPath() + "&target=" + URL.encodeQueryString(newFilePath));
			}				
			
			/*
			 * Creation of directories directly inside of the upload target should not be possible (possible name clash)
			 * Rename of target and files directly inside of target should not be possible (target no longer available, name clash)
			 * Delete of target should not be possible (target no longer available)
			 */
			
			// for now, just disable all of the others:
			disableManagement();
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
	
	@Override
	public void onSelect(FileImageLabelTreeItem selectedItem) {
		if(selectedItem != null) {
			setSystemFile(selectedItem.getFileInfo().isSystemFile());
			setAllowsChildren(selectedItem.getFileInfo().isAllowsNewChildren());
		}
	}

	private void initActions() {
		setAllowsChildren(false);
		setSystemFile(true);
	}
	
	private void disableManagement() {
		view.setEnabledCreateDirectory(false);
		view.setEnabledRename(false);
		view.setEnabledDelete(false);
		view.setEnabledUpload(false);
	}

	private void enableManagement() {
		view.setEnabledCreateDirectory(true);
		view.setEnabledRename(true);
		view.setEnabledDelete(true);
		view.setEnabledUpload(true);
	}
	
	private void setSystemFile(boolean systemFile) {
		view.setEnabledRename(!systemFile);
		view.setEnabledDelete(!systemFile);
	}	
	
	private void setAllowsChildren(boolean allowsNewChildren) {
		view.setEnabledCreateDirectory(allowsNewChildren);
		/*if(allowsNewChildren) {
			display.getUploader().getFileInput().getWidget().getElement().removeAttribute("aria-hidden");
			display.getAddButton().getElement().removeAttribute("aria-hidden");
		} else {
			display.getUploader().getFileInput().getWidget().getElement().setAttribute("aria-hidden", "true");
			display.getAddButton().getElement().setAttribute("aria-hidden", "true");
		}*/
		view.setEnabledUpload(allowsNewChildren);
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
	//moved to FileTreePresenter, when a folder is selected for uploading, set multiple attributes.
	/*public static native void setInputFileMultiple() /*-{
		var inputs, index;
		alert("$doc body:"+$doc.textContent);
	    inputs = $doc.getElementsByTagName("input");	
	    alert("get "+inputs.length+" inputs");
		for (index = 0; index < inputs.length; ++index) {
			if(inputs[index].getAttribute("type") == "file" && inputs[index].getAttribute("class") == "gwt-FileUpload") {
				inputs[index].setAttribute("multiple", "multiple");
				alert("set multiple in setInputFileMultiple");
			}
		}
	}-*/;

	@Override
	public IManagableFileTreeView getView() {
		return view;
	}
	
	
	/**
	 * The ButtonFileInput implementation sets the button invisble once an upload is active.
	 * As this is not the desired behavior for us this class overrides the default behavior.
	 * @author rodenhausen
	 */
	public class MyFileInput extends ButtonFileInput {
		public MyFileInput(Button addButton) {
			super(addButton);
		}

		
		@Override
		public void setVisible(boolean b) {
			//ignore visibility based on active upload
		}

	}


}