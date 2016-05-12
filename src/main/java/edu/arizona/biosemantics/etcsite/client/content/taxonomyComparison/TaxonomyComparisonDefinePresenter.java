package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView.Presenter;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;

public class TaxonomyComparisonDefinePresenter implements ITaxonomyComparisonDefineView.Presenter {

	private ITaxonomyComparisonDefineView view;
	private IFileServiceAsync fileService; 
	private PlaceController placeController;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private ITaxonomyComparisonInputView.Presenter createPresenter;
	private String ontologyInputFile;
	private String termReviewInputFile1;
	private String termReviewInputFile2;
	private String serializedModel1;
	private String serializedModel2;
	private String cleanTaxInput;
	private String serializedModelPath1;
	private String serializedModelPath2;
	private Presenter taxonomySelectionPresenter;
	
	@Inject
	public TaxonomyComparisonDefinePresenter(ITaxonomyComparisonDefineView view, 
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			IFileServiceAsync fileService,
			PlaceController placeController, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			@Named("TaxonomySelection")ISelectableFileTreeView.Presenter taxonomySelectionPresenter,
			FilePathShortener filePathShortener,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter,
			ITaxonomyComparisonInputView.Presenter createPresenter) {
		this.view = view;
		view.setPresenter(this);;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.fileService = fileService;
		this.placeController = placeController;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.taxonomySelectionPresenter = taxonomySelectionPresenter;
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
				view.getTaskName(), cleanTaxInput, view.getTaxonGroup(), ontologyInputFile, termReviewInputFile1, 
				termReviewInputFile2, new AsyncCallback<Task>() {
				@Override
				public void onSuccess(Task result) {
					placeController.goTo(new TaxonomyComparisonAlignPlace(result));
					Alerter.stopLoading(box);
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToStartTaxonomyComparison(caught);
					Alerter.stopLoading(box);
				}
			});
		} else if(createPresenter.isFromSerializedModel()) {
			if(view.getTaxonomy1Author().isEmpty() || view.getTaxonomy1Year().isEmpty() || 
					view.getTaxonomy2Author().isEmpty() || view.getTaxonomy2Year().isEmpty() ||
					//year can't be the same. see cleantax format and how year is used as id to in articulations
					//(view.getTaxonomy1Author().equals(view.getTaxonomy2Author()) && view.getTaxonomy1Year().equals(view.getTaxonomy2Year()))) {
					view.getTaxonomy1Year().equals(view.getTaxonomy2Year())) {
				Alerter.selectAuthorAndYears();
				return;
			}
			final MessageBox box = Alerter.startLoading();
			taxonomyComparisonService.startFromSerializedModels(Authentication.getInstance().getToken(), 
					view.getTaskName(), serializedModelPath1, serializedModelPath2, view.getTaxonomy1Author(), 
					view.getTaxonomy1Year(), view.getTaxonomy2Author(), view.getTaxonomy2Year(),
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
						Alerter.stopLoading(box);
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
					}else if(selection.getText().contains("[0 files")){
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
	public ITaxonomyComparisonDefineView getView() {
		return view;
	}

	@Override
	public void setSelectedSerializedModels(String model1, final String model2, final String modelPath1, final String modelPath2) {
		view.resetFields();
		this.cleanTaxInput = null;
		view.setCleanTaxPath(null);
		this.serializedModel1 = model1;
		this.serializedModel2 = model2;
		this.serializedModelPath1 = modelPath1;
		this.serializedModelPath2 = modelPath2;
		view.setSerializedModels(filePathShortener.shortenPath(serializedModelPath1), filePathShortener.shortenPath(serializedModelPath2));
		
		final MessageBox box = Alerter.startLoading();
		fileService.getTermReviewFileFromMatrixGenerationOutput(Authentication.getInstance().getToken(), modelPath1, new AsyncCallback<FileTreeItem>() {
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
				else
					view.setTermReviewPath1(null);
				fileService.getTermReviewFileFromMatrixGenerationOutput(Authentication.getInstance().getToken(), modelPath2, new AsyncCallback<FileTreeItem>() {
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
						else 
							view.setTermReviewPath2(null);
						fileService.getOntologyInputFileFromMatrixGenerationOutput(
								Authentication.getInstance().getToken(), modelPath2, new AsyncCallback<FileTreeItem>() {
									@Override
									public void onFailure(Throwable caught) {
										Alerter.stopLoading(box);
									}
									@Override
									public void onSuccess(FileTreeItem result) {
										if(result != null) {
											ontologyInputFile = result.getFilePath();
											view.setOntologyPath(result.getDisplayFilePath());
										}
										else
											view.setOntologyPath(null);
										Alerter.stopLoading(box);
									}
								});
					}
				});
			}
		});
	}

	@Override
	public void setSelectedCleanTaxFolder(String fullPath, String shortenedPath) {
		this.serializedModel1 = null;
		this.serializedModel2 = null;
		this.termReviewInputFile1 = null;
		this.termReviewInputFile2 = null;
		this.serializedModelPath1=null;
		this.serializedModelPath1=null;
		this.cleanTaxInput = fullPath;
		view.setCleanTaxPath(shortenedPath);
		view.resetFields();
	}
    
	@Override
	public void onExistingModel1() {
		showInput(new InputSetter() {
			@Override
			public void set(String input, String filePath) {
				serializedModelPath1= filePath;
				view.setSerializedModel1(input);	
				//view.setEnabledNext(isInputComplete());
			}
		});
	}
	
	@Override
	public void onExistingModel2() {
		showInput(new InputSetter() {
			@Override
			public void set(String input, String filePath) {
				serializedModelPath2= filePath;
				view.setSerializedModel2(input);	
				//view.setEnabledNext(isInputComplete());
			}
		});
	}
	
	/*@Override
	public void onExistingModel1() {
		taxonomySelectionPresenter.show("Select input", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect()  {
				List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
				if (selections.size() == 1) {
					FileTreeItem selection = selections.get(0);
					serializedModelPath1 = selection.getFilePath();
					serializedModel1 = filePathShortener.shortenPath(selection.getText());
					if(selection.isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					} else if(selection.getText().contains(" 0 file")) {
						Alerter.emptyFolder();
					} else {
						view.setSerializedModel1(filePathShortener.shortenPath(serializedModelPath1));
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

	/*@Override
	public void onExistingModel2() {
		taxonomySelectionPresenter.show("Select input", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect()  {
				List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
				if (selections.size() == 1) {
					FileTreeItem selection = selections.get(0);
					serializedModelPath2 = selection.getFilePath();
					serializedModel2 = selection.getText();
					if(selection.isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					} else if(selection.getText().contains(" 0 file")) {
						Alerter.emptyFolder();
					} else {
						view.setSerializedModel2(filePathShortener.shortenPath(serializedModelPath2));
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

	@Override
	public void onCleanTaxFolder() {
		this.showInput(new InputSetter() {
			@Override
			public void set(String input, String filePath) {
				cleanTaxInput = filePath;
				view.setCleanTaxPath(input);
			}
		});
	}
}
