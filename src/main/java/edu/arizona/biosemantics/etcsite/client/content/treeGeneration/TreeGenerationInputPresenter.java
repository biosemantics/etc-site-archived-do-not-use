package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FileImageLabelTreeItem;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;

public class TreeGenerationInputPresenter implements ITreeGenerationInputView.Presenter {

	private ITreeGenerationInputView view;
	private PlaceController placeController;
	private ITreeGenerationServiceAsync treeGenerationService;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private String inputFile;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	
	@Inject
	public TreeGenerationInputPresenter(ITreeGenerationInputView view, 
			ITreeGenerationServiceAsync treeGenerationService,
			PlaceController placeController, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			FilePathShortener filePathShortener,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter
			) {
		this.view = view;
		view.setPresenter(this);;
		this.treeGenerationService = treeGenerationService;
		this.placeController = placeController;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.filePathShortener = filePathShortener;
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
	}
	
	@Override
	public void onInputSelect() {
		selectableFileTreePresenter.show("Select input", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect() {
				FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
				if (selection != null) {
					inputFile = selection.getFileInfo().getFilePath();
					String shortendPath = filePathShortener.shorten(selection.getFileInfo(), Authentication.getInstance().getUserId());
					if(selection.getFileInfo().isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					}else if(selection.getText().contains(" 0 file")){
						Alerter.emptyFolder();
					}else{
					view.setFilePath(shortendPath);
					view.setEnabledNext(true);			
					if(selection.getFileInfo().getOwnerUserId() != Authentication.getInstance().getUserId()) {
						Alerter.sharedInputForTask();
						fileManagerDialogPresenter.hide();
					} else {
						fileManagerDialogPresenter.hide();
					}
					}
				}
			}
		});
	}

	@Override
	public void onFileManager() {
		fileManagerDialogPresenter.show();
	}

	@Override
	public void onNext() {
		if (inputFile == null || inputFile.equals("")){
			Alerter.selectValidInputDirectory();
			return;
		}
		if (view.getTaskName() == null || view.getTaskName().equals("")){
			Alerter.selectTaskName();
			return;
		}
		
		Alerter.startLoading();
		treeGenerationService.isValidInput(Authentication.getInstance().getToken(), inputFile, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if(!result) {
					Alerter.invalidInputDirectory();
					Alerter.stopLoading();
				} else {
					treeGenerationService.start(Authentication.getInstance().getToken(), 
							view.getTaskName(), inputFile, new AsyncCallback<Task>() {
								@Override
								public void onSuccess(Task result) {
									placeController.goTo(new TreeGenerationViewPlace(result));
									Alerter.stopLoading();
								}
								@Override
								public void onFailure(Throwable caught) {
									Alerter.failedToStartTreeGeneration(caught);
								}
					});
				}
				
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToIsValidInput(caught);
			}
		});
	}

	@Override
	public ITreeGenerationInputView getView() {
		view.resetFields();
		inputFile = null;
		return view;
	}

}
