package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.filemanager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.FilePathShortener;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.ISelectableFileTreeView.Presenter;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileFilter;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;
import edu.arizona.biosemantics.etcsite.core.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;

public class SemanticMarkupInputPresenter implements ISemanticMarkupInputView.Presenter {

	private ISemanticMarkupInputView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private String inputFile;
	private Presenter selectableFileTreePresenter;
	private edu.arizona.biosemantics.etcsite.filemanager.client.common.IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private edu.arizona.biosemantics.etcsite.client.content.filemanager.IFileManagerDialogView.Presenter fileManagerDialogPresenter;

	@Inject
	public SemanticMarkupInputPresenter(ISemanticMarkupInputView view, 
			PlaceController placeController, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			ISemanticMarkupServiceAsync semanticMarkupService, 
			FilePathShortener filePathShortener,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.filePathShortener = filePathShortener;
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
	}
	
	@Override
	public IsWidget getView() {
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
		
		// Check to see if input is too big.
		final MessageBox box = Alerter.startLoading();
		semanticMarkupService.isLargeInput(Authentication.getInstance().getToken(), inputFile, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean largeInput) {
				Alerter.stopLoading(box);
				if(largeInput) {
					final MessageBox box = Alerter.semanticMarkupWarnUserTooManyWords();
					box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							startSemanticMarkup();
						}
					});
				} else {
					startSemanticMarkup();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				Alerter.stopLoading(box);
			}
		});
		
		
	}
	
	private void startSemanticMarkup(){
		final MessageBox box = Alerter.startLoading();
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
				Alerter.stopLoading(box);
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToStartSemanticMarkup(caught);
				Alerter.stopLoading(box);
			}
		});
	}

	@Override
	public void setSelectedFolder(String fullPath, String shortendPath) {
		inputFile = fullPath;
		view.setInput(shortendPath);
	}

	@Override
	public void onInput() {
		selectableFileTreePresenter.show("Select input", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect() {
				List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
				if (selections.size() == 1) {
					FileTreeItem selection = selections.get(0);
					inputFile = selection.getFilePath();
					String shortendPath = filePathShortener.shorten(selection, Authentication.getInstance().getUserId());
					if(selection.isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					}else if(selection.getText().contains(" 0 file")){
						Alerter.emptyFolder();
					}else{
						view.setInput(shortendPath);
						view.setEnabledNext(true);	
						
						if(selection.getOwnerUserId() != Authentication.getInstance().getUserId()) {
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

}
