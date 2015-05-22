package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;

public class MatrixGenerationOutputPresenter implements IMatrixGenerationOutputView.Presenter {

	private Task task;
	private IMatrixGenerationOutputView view;
	private PlaceController placeController;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private FilePathShortener filePathShortener;

	@Inject
	public MatrixGenerationOutputPresenter(IMatrixGenerationOutputView view, 
			PlaceController placeController, 
			IMatrixGenerationServiceAsync matrixGenerationService, 
			FilePathShortener filePathShortener) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.matrixGenerationService = matrixGenerationService;
		this.filePathShortener = filePathShortener;
	}
	
	@Override
	public void onFileManager() {
		placeController.goTo(new FileManagerPlace());
	}

	@Override
	public IMatrixGenerationOutputView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		matrixGenerationService.output(Authentication.getInstance().getToken(), task, 
				new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				String output = ((MatrixGenerationConfiguration)result.getConfiguration()).getOutput();
				view.setOutput(filePathShortener.shortenOutput(output, result, Authentication.getInstance().getUserId()));
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToOutput(caught);
			}
		});
	}


}
