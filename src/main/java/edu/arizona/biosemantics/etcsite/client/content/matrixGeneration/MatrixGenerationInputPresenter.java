package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.client.common.MessagePresenter;
import edu.arizona.biosemantics.etcsite.client.common.files.FileImageLabelTreeItem;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupInputView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;

public class MatrixGenerationInputPresenter implements IMatrixGenerationInputView.Presenter {

	private IMatrixGenerationInputView view;
	private PlaceController placeController;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private String inputFile;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private MessagePresenter messagePresenter = new MessagePresenter();
	
	@Inject
	public MatrixGenerationInputPresenter(IMatrixGenerationInputView view, 
			IMatrixGenerationServiceAsync matrixGenerationService,
			PlaceController placeController, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			FilePathShortener filePathShortener,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter
			) {
		this.view = view;
		view.setPresenter(this);;
		this.matrixGenerationService = matrixGenerationService;
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
					view.setFilePath(shortendPath);
					view.setEnabledNext(true);			
					if(selection.getFileInfo().getOwnerUserId() != Authentication.getInstance().getUserId()) {
						messagePresenter.showOkBox("Shared input", "The selected input is not owned. To start the task the files will be copied to your own space.");
						fileManagerDialogPresenter.hide();
					} else {
						fileManagerDialogPresenter.hide();
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
		
		//error checking.
		if (inputFile == null || inputFile.equals("")){
			messagePresenter.showOkBox("", "Please enter a valid directory.");
			return;
		}
		if (view.getTaskName() == null || view.getTaskName().equals("")){
			messagePresenter.showOkBox("", "Please enter a name for this task.");
			return;
		}
		
		//end error checking.
		
		loadingPopup.start();
		matrixGenerationService.isValidInput(Authentication.getInstance().getToken(), inputFile, new RPCCallback<Boolean>() {
			@Override
			public void onResult(Boolean result) {
				if(!result) {
					messagePresenter.showOkBox("Input", "Not a valid input directory.");
					loadingPopup.stop();
				} else {
					matrixGenerationService.start(Authentication.getInstance().getToken(), 
							view.getTaskName(), inputFile, new RPCCallback<Task>() {
								@Override
								public void onResult(Task result) {
									placeController.goTo(new MatrixGenerationProcessPlace(result));
									loadingPopup.stop();
								}
					});
				}
				
			}
		});
	}

	@Override
	public IMatrixGenerationInputView getView() {
		view.resetFields();
		inputFile = null;
		return view;
	}

}
