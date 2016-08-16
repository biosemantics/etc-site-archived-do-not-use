package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
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
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;

public class OntologizeDefinePresenter implements IOntologizeDefineView.Presenter {

	private IOntologizeDefineView view;
	private PlaceController placeController;
	private IOntologizeServiceAsync ontologizeService;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private String inputFile;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private String ontologyFile;
	
	@Inject
	public OntologizeDefinePresenter(IOntologizeDefineView view, 
			IOntologizeServiceAsync ontologizeService,
			PlaceController placeController, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			FilePathShortener filePathShortener,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter
			) {
		this.view = view;
		view.setPresenter(this);;
		this.ontologizeService = ontologizeService;
		this.placeController = placeController;
		this.selectableFileTreePresenter = selectableFileTreePresenter;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.filePathShortener = filePathShortener;
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
	}
	

	@Override
	public void onNext() {
		final MessageBox box = Alerter.startLoading();
		validateInput(new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading(box);
				Alerter.failedToValidateInput(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				if(result) {
					ontologizeService.startWithOntologyCreation(Authentication.getInstance().getToken(), 
							view.getTaskName(), inputFile, view.getTaxonGroup()
							, new AsyncCallback<Task>() {
						@Override
						public void onSuccess(Task result) {
							placeController.goTo(new OntologizeBuildPlace(result));
							Alerter.stopLoading(box);
						}
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToStartOntologize(caught);
							Alerter.stopLoading(box);
						}
					});
					
					/*if (view.isCreateOntology()) {
						ontologizeService.startWithOntologyCreation(Authentication.getInstance().getToken(), 
								view.getTaskName(), inputFile, view.getTaxonGroup(), view.getOntologyPrefix()
								, new AsyncCallback<Task>() {
							@Override
							public void onSuccess(Task result) {
								placeController.goTo(new OntologizeBuildPlace(result));
								Alerter.stopLoading(box);
							}
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToStartOntologize(caught);
								Alerter.stopLoading(box);
							}
						});
					}
					if(view.isSelectOntology()) {
						ontologizeService.startWithOntologySelection(Authentication.getInstance().getToken(), 
								view.getTaskName(), inputFile, view.getTaxonGroup(), ontologyFile, new AsyncCallback<Task>() {
							@Override
							public void onSuccess(Task result) {
								placeController.goTo(new OntologizeBuildPlace(result));
								Alerter.stopLoading(box);
							}
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToStartOntologize(caught);
								Alerter.stopLoading(box);
							}
						});
					}*/
				} else {
					Alerter.stopLoading(box);
				}
			}
		});
	}
				

	private void validateInput(final AsyncCallback<Boolean> callback) {
		if (inputFile == null || inputFile.equals("")){
			Alerter.selectValidInputDirectory();
			callback.onSuccess(false);
			return;
		}
		if (view.getTaskName() == null || view.getTaskName().equals("")){
			Alerter.selectTaskName();
			callback.onSuccess(false);
			return;
		}
		
		/*if((!view.isCreateOntology() || (view.isCreateOntology() && view.getOntologyPrefix().isEmpty()))
				&& (!view.isSelectOntology() || 
				(view.isSelectOntology() && ontologyFile == null))) {
			Alerter.selectOrCreateOntology();
			callback.onSuccess(false);
			return;
		}*/
		
		ontologizeService.isValidInput(Authentication.getInstance().getToken(), 
				inputFile, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToValidateInput(caught);
					}
					@Override
					public void onSuccess(Boolean result) {
						/*if(view.isSelectOntology()) {
							ontologizeService.isValidOntology(Authentication.getInstance().getToken(), 
									ontologyFile, new AsyncCallback<Boolean>() {
										@Override
										public void onFailure(Throwable caught) {
											Alerter.failedToValidateOntology(caught);
										}
										@Override
										public void onSuccess(Boolean result) {
											callback.onSuccess(result);
										}
								
							});
						} else {*/
							callback.onSuccess(true);
						//}
					}
		});
	}


	@Override
	public IOntologizeDefineView getView() {
		return view;
	}
	
	@Override
	public void setSelectedFolder(String fullPath, String shortendPath) {
		inputFile = fullPath;
		view.setFilePath(shortendPath);
		view.resetFields();
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
					} else if(selection.getText().contains("[0 files")) {
						Alerter.emptyFolder();
				    } else if(!selection.getText().matches(".*?\\b0 director.*")){
					   Alerter.containSubFolder();
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

	/*@Override
	public void onOntologySelect() {
		selectableFileTreePresenter.show("Select ontology", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect() {
				FileImageLabelTreeItem selection = fileTreePresenter.getSelectedItem();
				if (selection != null) {
					ontologyFile = selection.getFileInfo().getFilePath();
					String shortendPath = filePathShortener.shorten(selection.getFileInfo(), Authentication.getInstance().getUserId());
					if(selection.getFileInfo().isSystemFile()){
						Alerter.systemFolderNotAllowedInputForTask();
					} else if(selection.getText().contains(" 0 file")) {
						Alerter.emptyFolder();
					} else {
						view.setOntologyFilePath(shortendPath);
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
	}*/
}
