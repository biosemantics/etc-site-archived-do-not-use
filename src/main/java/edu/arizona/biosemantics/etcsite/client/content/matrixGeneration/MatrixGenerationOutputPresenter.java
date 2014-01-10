package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.shared.db.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.file.FilePathShortener;
import edu.arizona.biosemantics.etcsite.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

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
				new RPCCallback<Task>() {
			@Override
			public void onResult(Task result) {
				String output = ((MatrixGenerationConfiguration)result.getConfiguration()).getOutput();
				view.setOutput(filePathShortener.shortenOutput(output, result, Authentication.getInstance().getUsername()));
			}
		});
	}


}
