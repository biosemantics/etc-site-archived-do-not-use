package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.filemanager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.core.shared.model.Task;
import edu.arizona.biosemantics.etcsite.core.shared.model.semanticmarkup.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.filemanager.client.common.FilePathShortener;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;

public class SemanticMarkupOutputPresenter implements ISemanticMarkupOutputView.Presenter {

	private ISemanticMarkupOutputView view;
	private PlaceController placeController;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private FilePathShortener filePathShortener;

	@Inject
	public SemanticMarkupOutputPresenter(ISemanticMarkupOutputView view, 
			PlaceController placeController, 
			ISemanticMarkupServiceAsync semanticMarkupService, 
			FilePathShortener filePathShortener) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.filePathShortener = filePathShortener;
	}
	
	@Override
	public void onFileManager() {
		placeController.goTo(new FileManagerPlace());
	}

	@Override
	public ISemanticMarkupOutputView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		semanticMarkupService.output(Authentication.getInstance().getToken(), task, 
				new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				String output = ((SemanticMarkupConfiguration)result.getConfiguration()).getOutput();
				view.setOutput(filePathShortener.shortenOutput(output, result, Authentication.getInstance().getUserId()));
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToOutput(caught);
			}
		});
	}

}
