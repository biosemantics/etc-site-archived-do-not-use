package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

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
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;

public class TaxonomyComparisonInputPresenter implements ITaxonomyComparisonInputView.Presenter {

	private ITaxonomyComparisonInputView view;
	private PlaceController placeController;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;
	private FilePathShortener filePathShortener;
	private String inputFile;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	
	@Inject
	public TaxonomyComparisonInputPresenter(ITaxonomyComparisonInputView view, 
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			PlaceController placeController, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter,
			FilePathShortener filePathShortener,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter
			) {
		this.view = view;
		view.setPresenter(this);;
		this.taxonomyComparisonService = taxonomyComparisonService;
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
		taxonomyComparisonService.start(Authentication.getInstance().getToken(), 
			view.getTaskName(), inputFile, new AsyncCallback<Task>() {
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
	}
	
	@Override
	public ITaxonomyComparisonInputView getView() {
		view.resetFields();
		return view;
	}

	@Override
	public void setSelectedFolder(String fullPath, String shortenedPath) {
		// TODO Auto-generated method stub
		inputFile = fullPath;
		view.setFilePath(shortenedPath);
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
					}else if(selection.getText().contains(" 0 file")){
						Alerter.emptyFolder();
					}else if(!selection.getText().matches(".*?\\b0 director.*")){
						Alerter.containSubFolder();
					}
					else{
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
