package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.shared.db.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.file.FilePathShortener;
import edu.arizona.biosemantics.etcsite.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

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
				new RPCCallback<Task>() {
			@Override
			public void onResult(Task result) {
				String output = ((SemanticMarkupConfiguration)result.getConfiguration()).getOutput();
				view.setOutput(filePathShortener.shortenOutput(output, result, Authentication.getInstance().getUsername()));
			}
		});
	}

}
