package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.IFileManagerDialogView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;

public class SemanticMarkupCreatePresenter implements SemanticMarkupCreateView.Presenter {

	private ISemanticMarkupCreateView view;
	private PlaceController placeController;
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
		view.setPresenter(this);
		inputCreatePresenter.setInputValidator(new IInputCreateView.InputValidator() {
			@Override
			public void validate(String inputFolderPath) {
				final MessageBox box = Alerter.startLoading();
				semanticMarkupService.isValidInput(Authentication.getInstance().getToken(), inputFolderPath, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if(!result) {
							Alerter.invalidInputDirectory();
							Alerter.stopLoading(box);
						} else {
							placeController.goTo(new SemanticMarkupInputPlace());
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
