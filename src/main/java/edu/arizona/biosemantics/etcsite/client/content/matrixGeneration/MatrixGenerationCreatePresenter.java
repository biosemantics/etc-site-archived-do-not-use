package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.InputValidator;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.Presenter;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.UploadCompleteHandler;
import edu.arizona.biosemantics.etcsite.client.content.filemanager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.core.shared.model.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;

public class MatrixGenerationCreatePresenter implements MatrixGenerationCreateView.Presenter {

	private IMatrixGenerationCreateView view;
	private edu.arizona.biosemantics.etcsite.client.content.filemanager.IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private IInputCreateView.Presenter inputCreatePresenter;

	@Inject
	public MatrixGenerationCreatePresenter(final PlaceController placeController, 
			IMatrixGenerationCreateView view, 
			@Named("MatrixGeneration") IInputCreateView.Presenter inputCreatePresenter,
			final IMatrixGenerationServiceAsync matrixGenerationService, 
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.view = view;
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		view.setPresenter(this);
		this.inputCreatePresenter = inputCreatePresenter;
		this.inputCreatePresenter.disableCreateFiles();
		this.inputCreatePresenter.addDummyCreateFiles();
		this.inputCreatePresenter.setNextButtonName("Next Step in Matrix Generation");
		inputCreatePresenter.setInputValidator(new InputValidator() {
			@Override
			public void validate(String inputFolderPath) {
				final MessageBox box = Alerter.startLoading();
				matrixGenerationService.checkInputValid(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						if(!result.equals("valid")) {
							Alerter.inputError(result);
							Alerter.stopLoading(box);
						} else {
							placeController.goTo(new MatrixGenerationInputPlace());
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
		inputCreatePresenter.setUploadCompleteHandler(new UploadCompleteHandler() {
			@Override
			public void handle(FileUploadHandler fileUploadHandler,IUploader uploader, String uploadDirectory) {
				if (uploader.getStatus() == Status.SUCCESS) {
					fileUploadHandler.validateTaxonDescriptionFiles(uploadDirectory);
				}
			}
		});
		inputCreatePresenter.setUploadFileType(FileTypeEnum.MARKED_UP_TAXON_DESCRIPTION);
	}
	
	@Override
	public IsWidget getView() {
		return view;
	}

	@Override
	public String getInputFolderPath() {
		return inputCreatePresenter.getInputFolderPath();
	}

	@Override
	public String getInputFolderShortenedPath() {
		return inputCreatePresenter.getInputFolderShortenedPath();
	}
	
	@Override
	public void onFileManager() {
		fileManagerDialogPresenter.show();
	}

	@Override
	public void refresh() {
		inputCreatePresenter.refreshFolders();
	}
}
