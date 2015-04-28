package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FileImageLabelTreeItem;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;

public class SemanticMarkupInputPresenter implements ISemanticMarkupInputView.Presenter {

	private ISemanticMarkupInputView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private String inputFile;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	ISemanticMarkupCreateView.Presenter  semanticMarkupCreatePresenter;

	@Inject
	public SemanticMarkupInputPresenter(ISemanticMarkupInputView view, 
			PlaceController 
			placeController, ISemanticMarkupServiceAsync semanticMarkupService, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			FilePathShortener filePathShortener,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter,
			ISemanticMarkupCreateView.Presenter  semanticMarkupCreatePresenter
			) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.filePathShortener = filePathShortener;
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		this.semanticMarkupCreatePresenter = semanticMarkupCreatePresenter;
		//view.setInput(semanticMarkupCreatePresenter.getInputFolder());
	}
	
	@Override
	public IsWidget getView() {
		view.resetFields();
		inputFile = null;
		return view;
	}

	@Override
	public void onNext() {
		//error checking.
		if (view.getTaskName().equals("")){
			Alerter.selectTaskName();
			return;
		}
		if (inputFile == null){
			Alerter.selectValidInputDirectory();
			return;
		}
		
		Alerter.startLoading();
		semanticMarkupService.isValidInput(Authentication.getInstance().getToken(), inputFile, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if(!result) {
					Alerter.invalidInputDirectory();
					Alerter.stopLoading();
				} else {
					semanticMarkupService.start(Authentication.getInstance().getToken(), 
							view.getTaskName(), inputFile, view.getGlossaryName(), view.isEmptyGlossarySelected(), new AsyncCallback<Task>() {
								@Override
								public void onSuccess(Task result) {
									switch(TaskStageEnum.valueOf(result.getTaskStage().getTaskStage())) {
									case LEARN_TERMS:
										placeController.goTo(new SemanticMarkupLearnPlace(result));
										break;
									default:
										placeController.goTo(new SemanticMarkupPreprocessPlace(result));
										break;
									}
									Alerter.stopLoading();
								}

								@Override
								public void onFailure(Throwable caught) {
									Alerter.failedToStartSemanticMarkup(caught);
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
	public void onInput() {
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
						view.setInput(shortendPath);
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

}
