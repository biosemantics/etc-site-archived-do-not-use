package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.shared.model.OntologizeConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.ontologize.IOntologizeServiceAsync;

public class OntologizeOutputPresenter implements IOntologizeOutputView.Presenter {

	private Task task;
	private IOntologizeOutputView view;
	private PlaceController placeController;
	private IOntologizeServiceAsync ontologizeService;
	private FilePathShortener filePathShortener;

	@Inject
	public OntologizeOutputPresenter(IOntologizeOutputView view, 
			PlaceController placeController, 
			IOntologizeServiceAsync ontologizeService, 
			FilePathShortener filePathShortener) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.ontologizeService = ontologizeService;
		this.filePathShortener = filePathShortener;
	}
	
	@Override
	public void onFileManager() {
		placeController.goTo(new FileManagerPlace());
	}

	@Override
	public IOntologizeOutputView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		ontologizeService.output(Authentication.getInstance().getToken(), task, 
				new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				String output = ((OntologizeConfiguration)result.getConfiguration()).getOutput();
				view.setOutput(filePathShortener.shortenOutput(output, result, Authentication.getInstance().getUserId()));
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToOutput(caught);
			}
		});
	}


}
