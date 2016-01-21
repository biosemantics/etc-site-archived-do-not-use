package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.filemanager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.FilePathShortener;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.IFileTreeView;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileFilter;
import edu.arizona.biosemantics.etcsite.filemanager.shared.model.FileTreeItem;
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
	public void onNext() {
		if (inputFile == null || inputFile.equals("")){
			Alerter.selectValidInputDirectory();
			return;
		}
		if (view.getTaskName() == null || view.getTaskName().equals("")){
			Alerter.selectTaskName();
			return;
		}
		
		final MessageBox box = Alerter.startLoading();
		treeGenerationService.start(Authentication.getInstance().getToken(), 
			view.getTaskName(), inputFile, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new TreeGenerationViewPlace(result));
				Alerter.stopLoading(box);
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToStartTreeGeneration(caught);
			}
		});
	}
				

	@Override
	public ITreeGenerationInputView getView() {
		return view;
	}
	
	@Override
	public void setSelectedFolder(String fullPath, String shortendPath) {
		inputFile = fullPath;
		view.setFilePath(shortendPath);
	}


	@Override
	public void onInputSelect() {
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
					} else if(selection.getText().contains(" 0 file")) {
						Alerter.emptyFolder();
					} else {
						view.setFilePath(shortendPath);
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
