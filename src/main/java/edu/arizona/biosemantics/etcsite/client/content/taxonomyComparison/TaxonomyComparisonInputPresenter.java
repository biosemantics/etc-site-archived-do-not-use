package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;

public class TaxonomyComparisonInputPresenter implements ITaxonomyComparisonInputView.Presenter {

	private ITaxonomyComparisonInputView view;
	private IFileServiceAsync fileService; 
	private PlaceController placeController;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private ITaxonomyComparisonCreateView.Presenter createPresenter;
	private String ontologyInputFile;
	private String termReviewInputFile1;
	private String termReviewInputFile2;
	
	@Inject
	public TaxonomyComparisonInputPresenter(ITaxonomyComparisonInputView view, 
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			IFileServiceAsync fileService,
			PlaceController placeController, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			FilePathShortener filePathShortener,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter,
			ITaxonomyComparisonCreateView.Presenter createPresenter) {
		this.view = view;
		view.setPresenter(this);;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.fileService = fileService;
		this.placeController = placeController;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.filePathShortener = filePathShortener;
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		this.createPresenter = createPresenter;
	}
	
	@Override
	public void onNext() {
		if (view.getTaskName() == null || view.getTaskName().equals("")){
			Alerter.selectTaskName();
			return;
		}
		
		if(createPresenter.isFromCleantax()) {
			final MessageBox box = Alerter.startLoading();
			taxonomyComparisonService.startFromCleantax(Authentication.getInstance().getToken(), 
				view.getTaskName(), createPresenter.getCleanTaxInputFolderPath(), view.getTaxonGroup(), ontologyInputFile, termReviewInputFile1, 
				termReviewInputFile2, new AsyncCallback<Task>() {
				@Override
				public void onSuccess(Task result) {
					placeController.goTo(new TaxonomyComparisonAlignPlace(result));
					Alerter.stopLoading(box);
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToStartTaxonomyComparison(caught);
				}
			});
		} else if(createPresenter.isFromSerializedModel()) {
			final MessageBox box = Alerter.startLoading();
			taxonomyComparisonService.startFromSerializedModels(Authentication.getInstance().getToken(), 
					view.getTaskName(), createPresenter.getInputFolderPath1(), createPresenter.getInputFolderPath2(), 
					view.getTaxonGroup(), ontologyInputFile, termReviewInputFile1, termReviewInputFile2, 
					new AsyncCallback<Task>() {
					@Override
					public void onSuccess(Task result) {
						placeController.goTo(new TaxonomyComparisonAlignPlace(result));
						Alerter.stopLoading(box);
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToStartTaxonomyComparison(caught);
					}
				});
		} else {
			Alerter.selectValidInputDirectory();
		}		
	}
	
	@Override
	public void onTermReviewInput1() {
		showInput(new InputSetter() {
			@Override
			public void set(String input, String filePath) {
				termReviewInputFile1 = filePath;
				view.setTermReviewPath1(input);	
				//view.setEnabledNext(isInputComplete());
			}
		});
	}
	
	@Override
	public void onTermReviewInput2() {
		showInput(new InputSetter() {
			@Override
			public void set(String input, String filePath) {
				termReviewInputFile2 = filePath;
				view.setTermReviewPath2(input);	
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
	public ITaxonomyComparisonInputView getView() {
		return view;
	}

	@Override
	public void setSelectedSerializedModels(String inputFolderPath1, final String inputFolderPath2) {
		final MessageBox box = Alerter.startLoading();
		fileService.getTermReviewFileTreeItem(Authentication.getInstance().getToken(), inputFolderPath1, new AsyncCallback<FileTreeItem>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(FileTreeItem fileTreeItem) {
				if(fileTreeItem != null) {
					termReviewInputFile1 = fileTreeItem.getFilePath();
					view.setTermReviewPath1(fileTreeItem.getDisplayFilePath());
				}
				fileService.getTermReviewFileTreeItem(Authentication.getInstance().getToken(), inputFolderPath2, new AsyncCallback<FileTreeItem>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.stopLoading(box);
					}
					@Override
					public void onSuccess(FileTreeItem fileTreeItem) {
						if(fileTreeItem != null) {
							termReviewInputFile2 = fileTreeItem.getFilePath();
							view.setTermReviewPath2(fileTreeItem.getDisplayFilePath());
						}
						Alerter.stopLoading(box);
					}
				});
			}
		});
	}

	/*@Override
	public void setSelectedFolder(String fullPath, String shortenedPath) {
		// TODO Auto-generated method stub
		inputFile = fullPath;
		//view.setFilePath(shortenedPath);
	}*/
	
	/*@Override
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
					}else if(selection.getText().contains(" 0 file")){
						Alerter.emptyFolder();
					}else if(!selection.getText().matches(".*?\\b0 director.*")){
						Alerter.containSubFolder();
					}
					else{
						//view.setFilePath(shortendPath);
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
	}*/
}
