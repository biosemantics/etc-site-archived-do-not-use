package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.Configuration;
import edu.arizona.biosemantics.etcsite.client.common.IMessageView;
import edu.arizona.biosemantics.etcsite.client.common.ITextInputListener;
import edu.arizona.biosemantics.etcsite.client.common.ITextInputView;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.client.common.MessageView;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.IFileTreeSelectionListener;
import edu.arizona.biosemantics.etcsite.shared.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;
import gwtupload.client.BaseUploadStatus;
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
	
	@Inject
	public ManagableFileTreePresenter(IManagableFileTreeView view, 
			IFileTreeView.Presenter fileTreePresenter, 
			IFileServiceAsync fileService, MessageView.Presenter messagePresenter, 
			ITextInputView.Presenter textInputPresenter) {
		this.fileService = fileService;
		this.view = view;
		this.view.setPresenter(this);
		this.fileTreePresenter = fileTreePresenter;
		fileTreePresenter.addSelectionListener(this);
		this.messagePresenter = messagePresenter;
		this.textInputPresenter = textInputPresenter;
		
		this.defaultServletPath = view.getUploader().getServletPath() + "?username=" + Authentication.getInstance().getUsername()
				+ "&sessionID=" + Authentication.getInstance().getSessionID();
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
		setInputFileMultiple();
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
									fileService.createDirectory(Authentication.getInstance().getToken(), newDirectoryParent, directoryName, 
											new RPCCallback<Void>() {
												@Override
												public void onResult(Void result) {
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
				fileService.getDownloadPath(Authentication.getInstance().getToken(), selectionPath, new RPCCallback<String>() {
					@Override
					public void onResult(String result) {
						//target=" + result.getData() + "&directory=yes
						Window.Location.replace(URL.encode("/etcsite/download/?target=" + result + "&username=" + Authentication.getInstance().getUsername() + "&" + 
								"sessionID=" + Authentication.getInstance().getSessionID()));
						
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
	
	public class OnFinishUploadHandler implements OnFinishUploaderHandler {
		@Override
		public void onFinish(IUploader uploader) {	
			
			String serverResponse = uploader.getServerInfo().message;
			if(serverResponse != null && !serverResponse.isEmpty()) {
				serverResponse = serverResponse.replaceAll("\n", "<br>");
				messagePresenter.showMessage("File Manager", serverResponse);
			}
			
			if (uploader.getStatus() == Status.SUCCESS) {
				uploader.getStatusWidget().setFileName("Done uploading.");
				fileTreePresenter.refresh(fileFilter);
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
			uploader.getStatusWidget().setFileName("Uploading, please wait...");
			final FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
			if(selection.getFileInfo().getFileType().equals(FileTypeEnum.DIRECTORY)) {
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
	public static native void setInputFileMultiple() /*-{
		var inputs, index;
		inputs = $doc.getElementsByTagName('input');	
		for (index = 0; index < inputs.length; ++index) {
			if(inputs[index].getAttribute("type") == "file" && inputs[index].getAttribute("class") == "gwt-FileUpload") {
				inputs[index].setAttribute("multiple", "multiple");
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
