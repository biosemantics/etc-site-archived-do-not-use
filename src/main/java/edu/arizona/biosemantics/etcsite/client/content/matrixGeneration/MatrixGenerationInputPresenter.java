package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

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
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;

public class MatrixGenerationInputPresenter implements IMatrixGenerationInputView.Presenter {

	private IMatrixGenerationInputView view;
	private PlaceController placeController;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private String inputFile;
	private String ontologyInputFile;
	private String termReviewInputFile;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	
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
	
	private static interface InputSetter {
		
		public void set(String input, String filePath);
		
	}
	
	private void showInput(final InputSetter inputSetter) {
		selectableFileTreePresenter.show("Select input", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect() {
				List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
				if (selections.size() == 1) {
					FileTreeItem selection = selections.get(0);
					String shortendPath = filePathShortener.shorten(selection, Authentication.getInstance().getUserId());
					if(selection.isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					}else if(selection.getText().contains(" 0 file")){
						Alerter.emptyFolder();
					}else if(!selection.getText().matches(".*?\\b0 director.*")){
						Alerter.containSubFolder();
					}
					else{
						inputSetter.set(shortendPath, selection.getFilePath());
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
	
	@Override
	public void onInputSelect() {
		showInput(new InputSetter() {
			@Override
			public void set(String input, String filePath) {
				inputFile = filePath;
				view.setFilePath(input);	
				//view.setEnabledNext(isInputComplete());
			}
		});
	}
	
	protected boolean isInputComplete() {
		return view.hasInput() && view.hasOntologyPath() && view.hasTermReview() && view.hasTaskName();
	}

	@Override
	public void onTermReviewInput() {
		showInput(new InputSetter() {
			@Override
			public void set(String input, String filePath) {
				termReviewInputFile = filePath;
				view.setTermReviewPath(input);	
				//view.setEnabledNext(isInputComplete());
			}
		});
	}
	
	@Override
	public void onOntologyInput() {
		showInput(new InputSetter() {
			@Override
			public void set(String input, String filePath) {
				ontologyInputFile = filePath;
				view.setOntologyPath(input);
				//view.setEnabledNext(isInputComplete());
			}
		});
	}

	@Override
	public void onNext() {
		if (inputFile == null || inputFile.isEmpty()) {
			Alerter.selectValidInputDirectory();
			return;
		}
		/*if(ontologyInputFile == null || ontologyInputFile.isEmpty()) {
			Alerter.selectValidInputOntology();
			return;
		}*/
		if (view.getTaskName() == null || view.getTaskName().equals("")){
			Alerter.selectTaskName();
			return;
		}
		
		final MessageBox box = Alerter.startLoading();
		matrixGenerationService.start(Authentication.getInstance().getToken(), 
			view.getTaskName(), inputFile, termReviewInputFile, ontologyInputFile,
				view.getTaxonGroup(), view.isInheritValues(), view.isGenerateAbsentPresent(), new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new MatrixGenerationProcessPlace(result));
				Alerter.stopLoading(box);
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToStartMatrixGeneration(caught);
			}
		});
	}
				
	@Override
	public IMatrixGenerationInputView getView() {
		return view;
	}

	@Override
	public void setSelectedFolder(String fullPath, String shortendPath) {
		inputFile = fullPath;
		view.setFilePath(shortendPath);
		view.setOntologyPath("");
		view.setTermReviewPath("");
		this.ontologyInputFile = "";
		this.termReviewInputFile = "";
		//view.setEnabledNext(false);
	}

}
