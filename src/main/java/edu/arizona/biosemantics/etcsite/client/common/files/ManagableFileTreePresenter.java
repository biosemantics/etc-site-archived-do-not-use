
package edu.arizona.biosemantics.etcsite.client.common.files;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.MyWindow;
import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesDialogPresenter.ICloseHandler;
import edu.arizona.biosemantics.etcsite.shared.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileSource;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import gwtupload.client.BaseUploadStatus;
import gwtupload.client.IFileInput.ButtonFileInput;
import gwtupload.client.IUploadStatus;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.OnStartUploaderHandler;

public class ManagableFileTreePresenter implements IManagableFileTreeView.Presenter, SelectionChangedHandler<FileTreeItem> {

	private IFileServiceAsync fileService;
	private IManagableFileTreeView view;
	private IFileTreeView.Presenter fileTreePresenter;
	private FileFilter fileFilter;
	private String defaultServletPath;
	private FileTreeItem targetFileTreeItem;
	private String targetUploadDirectory;
	private ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter;
	private FileUploadHandler fileUploadHandler;
	private String uploadFileType;
	
	@SuppressWarnings("deprecation")
	@Inject
	public ManagableFileTreePresenter(IManagableFileTreeView view, 
			IFileTreeView.Presenter fileTreePresenter, 
			IFileServiceAsync fileService,  
			ICreateSemanticMarkupFilesDialogView.Presenter createSemanticMarkupFilesDialogPresenter) {
		this.fileService = fileService;
		this.view = view;
		this.view.setPresenter(this);
		this.fileTreePresenter = fileTreePresenter;
		fileTreePresenter.getView().addSelectionChangeHandler(this);
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
		fileUploadHandler = new FileUploadHandler(this, fileService);
		uploadFileType = "";
		initActions();
	}
	
	@Override
	public void refresh(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
		fileTreePresenter.getView().refresh(fileFilter);
		//setInputFileMultiple();
	}
	
	@Override
	public void onCreateSemanticMarkupFiles() {
		final FileTreeItem selection = fileTreePresenter.getView().getSelection().get(0);
		createSemanticMarkupFilesDialogPresenter.setCloseHandler(new ICloseHandler() {
			@Override
			public void onClose(int filesCreated) {
				if(filesCreated > 0)
					fileTreePresenter.getView().refreshNode(selection, fileFilter);
			}
		});
		createSemanticMarkupFilesDialogPresenter.show(fileTreePresenter.getView().getSelection().get(0).getFilePath());
	}

	@Override
	public void onCreate() {
		final List<FileTreeItem> selection = fileTreePresenter.getView().getSelection();
		if(selection.size() == 1) {
			int level = fileTreePresenter.getView().getDepth(selection.get(0));
			if(level < Configuration.fileManagerMaxDepth) {
				 final PromptMessageBox box = new PromptMessageBox("Create folder", "Folder name:");
				 box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						createDirectory(box.getValue());
					}
				 });
				 box.getTextField().addKeyPressHandler(new KeyPressHandler() {
					
					@Override
					public void onKeyPress(KeyPressEvent event) {
						if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
							createDirectory(box.getValue());
							box.hide();
						}
					}
				});
				 box.show();
			} else {
				Alerter.maxDepthReached();
			}
		} else {
			Alerter.invalidParentDirectory();
		}
	}

	private void createDirectory(final String folderName){
		final FileTreeItem selection = fileTreePresenter.getView().getSelection().get(0);
		final String selectionPath = selection.getFilePath();
		fileService.createDirectory(Authentication.getInstance().getToken(), selectionPath, folderName, false,
				new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						fileTreePresenter.getView().refreshNode(selection, fileFilter);
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToCreateDirectory(caught);
					}
		});
	}
	
	private void renameFile(final FileTreeItem selection, final String newName){
		if(selection instanceof FolderTreeItem) {
			fileService.renameFile(Authentication.getInstance().getToken(), selection.getFilePath(), 
				newName, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToRenameFile(caught);
					}
					@Override
					public void onSuccess(Void result) {
						fileTreePresenter.getView().refreshNode(selection, fileFilter);
						selection.setName(newName);
					}
			});
		} else {
			final String newFileName = selection.getExtension().isEmpty() ? 
					newName : newName + "." + selection.getExtension();
			fileService.renameFile(Authentication.getInstance().getToken(), selection.getFilePath(), 
					newFileName, new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					fileTreePresenter.getView().refreshNode(selection, fileFilter);
					selection.setName(newFileName);
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToRenameFile(caught);
				}
			});
		}
	}

	@Override
	public void onRename() {
		final FileTreeItem selection = fileTreePresenter.getView().getSelection().get(0);
		//don't allow rename of root node
		final PromptMessageBox box = new PromptMessageBox("Rename", "New name:");
		box.getButton(PredefinedButton.OK).addSelectHandler(
				new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						renameFile(selection, box.getValue());
					}
				});
		box.getTextField().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					renameFile(selection, box.getValue());
					box.hide();
				}
			}
		});
		box.getTextField().setValue(selection.getName(false));
		box.show();
	}

	@Override
	public void onDelete() {
		final List<FileTreeItem> selection = new LinkedList<FileTreeItem>(fileTreePresenter.getView().getSelection());
		MessageBox delete = Alerter.sureToDelete(getDeleteText(selection));
		delete.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final MessageBox box = Alerter.startLoading();
				fileService.deleteFiles(Authentication.getInstance().getToken(), selection, new AsyncCallback<Void>(){
					@Override
					public void onSuccess(Void result) {
						ManagableFileTreePresenter.this.initActions();
						Set<FileTreeItem> parents = new HashSet<FileTreeItem>();
						for(FileTreeItem item : selection)
							parents.add(fileTreePresenter.getView().getParent(item));
						for(FileTreeItem parent : parents)
							fileTreePresenter.getView().refreshNode(parent, fileFilter);
						Alerter.stopLoading(box);
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToDeleteFile(caught);
						Alerter.stopLoading(box);
					}
				});
			}
		});
	}

	private String getDeleteText(List<FileTreeItem> items) {
		String result = "";
		for(FileTreeItem item : items) {
			result += item.getName(true) + ", ";
		}
		if(!result.isEmpty())
			return result.substring(0, result.length() - 2);
		return result;
	}

	@Override
	public void onDownload() {
		final List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
		if(!selections.isEmpty()) {  
			if(selections.size() == 1) {
				final String selectionPath = selections.get(0).getFilePath();
				if(selectionPath != null) {
					final MessageBox box = Alerter.startLoading();
					final MyWindow window = MyWindow.open(null, "_blank", null);
					fileService.getDownloadPath(Authentication.getInstance().getToken(), selectionPath, new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							//target=" + result.getData() + "&directory=yes
							Alerter.stopLoading(box);
							window.setUrl("download.dld?target=" + URL.encodeQueryString(result) + 
									"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
									"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()));
							/*Window.open("download.dld?target=" + URL.encodeQueryString(result) + 
									"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
									"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()), "_blank", "");
							/*Window.Location.replace("/etcsite/download?target=" + URL.encodeQueryString(result) + 
									"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
									"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()));*/
							
							/*Window.open("/etcsite/download/?target=" + result.getData() + "&username=" + Authentication.getInstance().getUsername() + "&" + 
									"sessionID=" + Authentication.getInstance().getSessionID()
									, "download", "resizable=yes,scrollbars=yes,menubar=yes,location=yes,status=yes"); */
						}
	
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToGetDownloadPath(caught);
							Alerter.stopLoading(box);
						}
					});
				} else {
					Alerter.notDownloadable();
				}
			} else {
				Alerter.notDownloadable();
			}
		} else {
			Alerter.selectFileToDownload();
		}
	}
	
	/**
	 * multiple file uploading is enabled by using the SingleUploader and the native method setInputFileMultiple() called in FileTreePresenter.java
	 * @author updates
	 *
	 */	
	public class OnFinishUploadHandler implements OnFinishUploaderHandler {
		String serverResponse = null;
		@Override
		public void onFinish(IUploader uploader) {
			serverResponse = fileUploadHandler.parseServerResponse(uploader);
			if (uploader.getStatus() == Status.SUCCESS) {
				// aggregate server response and display the result at the end of key validation for taxon_description
				// and marked_up_taxon_description files
				if(uploadFileType.equals(FileTypeEnum.TAXON_DESCRIPTION.displayName()) || 
						uploadFileType.equals(FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION.displayName())){
					fileUploadHandler.validateTaxonDescriptionFiles(targetUploadDirectory);
				} else {
					if(serverResponse != null && !serverResponse.isEmpty())
						Alerter.failedToUpload(serverResponse);
				}
				fileTreePresenter.getView().refreshNode(targetFileTreeItem, fileFilter);
			}else{
				if(serverResponse != null && !serverResponse.isEmpty())
					Alerter.failedToUpload(serverResponse);
			}
			uploader.setServletPath(defaultServletPath);
			enableManagement();
		}
	}
	
	

	public class OnStartUploadHandler implements OnStartUploaderHandler {
		@Override
		public void onStart(final IUploader uploader) {
			final List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
			if(selections.size() == 1) {
				targetFileTreeItem = selections.get(0);
				targetUploadDirectory = selections.get(0).getFilePath();
				uploadFileType = view.getFormat();
				fileUploadHandler.setServletPathOfUploader(uploader, view.getFormat(), targetUploadDirectory);
				/*
				 * Creation of directories directly inside of the upload target should not be possible (possible name clash)
				 * Rename of target and files directly inside of target should not be possible (target no longer available, name clash)
				 * Delete of target should not be possible (target no longer available)
				 */
				
				// for now, just disable all of the others:
				disableManagement();
			} else {
				uploader.cancel();
			}
		}
	}

	private void initActions() {
		view.setEnabledCreateDirectory(false);
		view.setEnabledUpload(false);
		view.setEnabledCreateSemanticMarkupFiles(false);
		view.setEnabledRename(false);
		view.setEnabledDelete(false);
	}
	
	private void disableManagement() {
		view.setEnabledCreateDirectory(false);
		view.setEnabledRename(false);
		view.setEnabledDelete(false);
		view.setEnabledUpload(false);
		view.setEnabledCreateSemanticMarkupFiles(false);
	}

	private void enableManagement() {
		view.setEnabledCreateDirectory(true);
		view.setEnabledRename(true);
		view.setEnabledDelete(true);
		view.setEnabledUpload(true);
		view.setEnabledCreateSemanticMarkupFiles(true);
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

	@Override
	public void onSelectionChanged(SelectionChangedEvent<FileTreeItem> event) {
		List<FileTreeItem> selections = event.getSelection();
		boolean singleSelection = selections.size() == 1;
		boolean nonZeroSelection = !selections.isEmpty();
		FileTreeItem firstItem = event.getSelection().get(0);
		
		boolean anySystemOrSharedOrPublicFile = false;
		for(FileTreeItem selection : selections) 
			if(selection.isSystemFile() || selection.getFileSource().equals(FileSource.PUBLIC) || 
					selection.getFileSource().equals(FileSource.SHARED))
				anySystemOrSharedOrPublicFile = true;
		
		view.setEnabledCreateDirectory(singleSelection && firstItem.isAllowsNewFolders());
		view.setEnabledUpload(singleSelection && firstItem.isAllowsNewFiles() && firstItem.isAllowsNewFolders());
		view.setEnabledCreateSemanticMarkupFiles(singleSelection && firstItem.isAllowsNewFiles());
		view.setEnabledRename(singleSelection && !firstItem.isSystemFile() 
				&& !firstItem.getFileSource().equals(FileSource.PUBLIC)
				&& !firstItem.getFileSource().equals(FileSource.SHARED));
		view.setEnabledDelete(nonZeroSelection && !anySystemOrSharedOrPublicFile);
		
		setInputFileMultiple();
	}
	
	public static native void setInputFileMultiple() /*-{
	    var inputs, index;
	    inputs = $doc.getElementsByTagName("input");    
	    //alert("get "+inputs.length+" inputs");
	    for (index = 0; index < inputs.length; ++index) {
	        if(inputs[index].getAttribute("type") == "file" && inputs[index].getAttribute("class") == "gwt-FileUpload") {
	            inputs[index].setAttribute("multiple", "multiple");
//	            alert("set multiple in setInputFileMultiple");
	        }
	    }
	}-*/;


}
