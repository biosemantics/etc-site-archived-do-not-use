package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;

public class TaxonomyComparisonInputPresenter implements ITaxonomyComparisonInputView.Presenter {

	private ITaxonomyComparisonInputView view;
	private PlaceController placeController;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private String inputFile;
	
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

}
