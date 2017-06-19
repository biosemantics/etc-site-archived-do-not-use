package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;

//import org.eclipse.jdt.internal.codeassist.select.SelectionScanner;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ManagableFileTreePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView.Presenter;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.Description;

public class SemanticMarkupDefinePresenter implements ISemanticMarkupDefineView.Presenter {

	private ISemanticMarkupDefineView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private String inputFile;
	private Presenter selectableFileTreePresenter;
	private edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView.Presenter fileManagerDialogPresenter;

	@Inject
	public SemanticMarkupDefinePresenter(ISemanticMarkupDefineView view, 
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
					case PARSE_TEXT:
						placeController.goTo(new SemanticMarkupParsePlace(result));
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
		view.resetFields();
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
					}else if(selection.getText().contains("[0 files")){
						Alerter.emptyFolder();
					} else if(!selection.getText().matches(".*?\\b0 director.*")){
						Alerter.containSubFolder();
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
