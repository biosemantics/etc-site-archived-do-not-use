package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IInputCreateView.InputValidator;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;

public class TaxonomyComparisonCreatePresenter implements TaxonomyComparisonCreateView.Presenter {

	private ITaxonomyComparisonCreateView view;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private IInputCreateView.Presenter inputCreatePresenter;

	@Inject
	public TaxonomyComparisonCreatePresenter(final PlaceController placeController, 
			ITaxonomyComparisonCreateView view, 
			IInputCreateView.Presenter inputCreatePresenter,
			final ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		
		this.inputCreatePresenter = inputCreatePresenter;
		this.inputCreatePresenter.setNextButtonName("Next Step in Taxonomy Comparison");
		inputCreatePresenter.setCleanTaxInputValidator(new InputValidator() {
			@Override
			public void validate(String inputFolderPath) {
				final MessageBox box = Alerter.startLoading();
				taxonomyComparisonService.isValidCleanTaxInput(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if(!result) {
							Alerter.invalidInputDirectory();
							Alerter.stopLoading(box);
						} else {
							placeController.goTo(new TaxonomyComparisonInputPlace());
							Alerter.stopLoading(box);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToIsValidInput(caught);
						Alerter.stopLoading(box);
					}
				});
			}
		});
		inputCreatePresenter.setModelInputValidator(new InputValidator() {
			@Override
			public void validate(String inputFolderPath) {
				final MessageBox box = Alerter.startLoading();
				taxonomyComparisonService.isValidModelInput(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if(!result) {
							Alerter.invalidInputDirectory();
							Alerter.stopLoading(box);
						} else {
							placeController.goTo(new TaxonomyComparisonInputPlace());
							Alerter.stopLoading(box);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToIsValidInput(caught);
						Alerter.stopLoading(box);
					}
				});
			}
		});
		inputCreatePresenter.setUploadFileType(FileTypeEnum.CLEANTAX);
	}
	
	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public void onFileManager() {
		fileManagerDialogPresenter.show();
	}

	@Override
	public String getCleanTaxInputFolderPath() {
		return inputCreatePresenter.getCleanTaxInputFolderPath();
	}

	@Override
	public String getCleanTaxInputFolderShortenedPath() {
		return inputCreatePresenter.getCleanTaxInputFolderShortenedPath();
	}

	@Override
	public void refresh() {
		this.inputCreatePresenter.refreshFolders();
	}

	@Override
	public boolean isFromCleantax() {
		return !view.getInputCreateView().isSelectExistingFolder();
	}

	@Override
	public boolean isFromSerializedModel() {
		return view.getInputCreateView().isSelectExistingFolder();
	}

	@Override
	public String getInputFolderPath1() {
		return inputCreatePresenter.getModelInputFolderPath1();
	}

	@Override
	public String getInputFolderPath2() {
		return inputCreatePresenter.getModelInputFolderPath2();
	}
}
