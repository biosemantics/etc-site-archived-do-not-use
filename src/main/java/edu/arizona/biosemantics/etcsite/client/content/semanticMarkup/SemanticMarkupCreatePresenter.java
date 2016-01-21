package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.UploadCompleteHandler;
import edu.arizona.biosemantics.etcsite.client.content.filemanager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.core.shared.model.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.FileUploadHandler;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;

public class SemanticMarkupCreatePresenter implements SemanticMarkupCreateView.Presenter {

	private ISemanticMarkupCreateView view;
	private IFileManagerDialogView.Presenter fileManagerDialogPresenter;
	private IInputCreateView.Presenter inputCreatePresenter;
	
	@Inject
	public SemanticMarkupCreatePresenter(final PlaceController placeController,
			ISemanticMarkupCreateView view, 
			@Named("SemanticMarkup") IInputCreateView.Presenter inputCreatePresenter,
			final ISemanticMarkupServiceAsync semanticMarkupService, 
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.view = view;
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		this.inputCreatePresenter = inputCreatePresenter;
		this.inputCreatePresenter.setNextButtonName("Next Step in Text Capture");
		view.setPresenter(this);
		inputCreatePresenter.setInputValidator(new IInputCreateView.InputValidator() {
			@Override
			public void validate(String inputFolderPath) {
				final MessageBox box = Alerter.startLoading();
				semanticMarkupService.checkValidInput(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						if(result.equals("valid")) {
							placeController.goTo(new SemanticMarkupInputPlace());
							Alerter.stopLoading(box);
						} else {
							Alerter.inputError(result);
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
			public void handle(FileUploadHandler fileUploadHandler,	IUploader uploader, String uploadDirectory) {
				if (uploader.getStatus() == Status.SUCCESS) {
					fileUploadHandler.validateTaxonDescriptionFiles(uploadDirectory);
				}
			}
		});
		inputCreatePresenter.setUploadFileType(FileTypeEnum.TAXON_DESCRIPTION);
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
	public String getInputFolderPath() {
		return inputCreatePresenter.getInputFolderPath();
	}

	@Override
	public String getInputFolderShortenedPath() {
		return inputCreatePresenter.getInputFolderShortenedPath();
	}

	@Override
	public void refresh() {
		inputCreatePresenter.refreshFolders();
	}

}
