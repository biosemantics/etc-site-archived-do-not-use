package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

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
import edu.arizona.biosemantics.etcsite.client.content.filemanager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.client.content.filemanager.IFileManagerDialogView.Presenter;
import edu.arizona.biosemantics.etcsite.core.shared.model.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;

public class TreeGenerationCreatePresenter implements TreeGenerationCreateView.Presenter{

	private ITreeGenerationCreateView view;
	private Presenter fileManagerDialogPresenter;
	private edu.arizona.biosemantics.etcsite.client.common.IInputCreateView.Presenter inputCreatePresenter;
	
	@Inject
	public TreeGenerationCreatePresenter(final PlaceController placeController, 
			ITreeGenerationCreateView view, 
			@Named("TreeGeneration") IInputCreateView.Presenter inputCreatePresenter,
			final ITreeGenerationServiceAsync treeGenerationService,
			IFileManagerDialogView.Presenter fileManagerDialogPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.fileManagerDialogPresenter = fileManagerDialogPresenter;
		
		this.inputCreatePresenter = inputCreatePresenter;
		this.inputCreatePresenter.disableCreateFiles();
		this.inputCreatePresenter.setNextButtonName("Next Step in Key Generation");
		inputCreatePresenter.setInputValidator(new InputValidator() {
			@Override
			public void validate(String inputFolderPath) {
				final MessageBox box = Alerter.startLoading();
				treeGenerationService.isValidInput(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if(!result) {
							Alerter.invalidInputDirectory();
							Alerter.stopLoading(box);
						} else {
							placeController.goTo(new TreeGenerationInputPlace());
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
		inputCreatePresenter.setUploadFileType(FileTypeEnum.PLAIN_TEXT);
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
