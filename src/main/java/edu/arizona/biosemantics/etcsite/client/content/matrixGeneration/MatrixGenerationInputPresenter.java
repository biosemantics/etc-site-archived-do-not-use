package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;

public class MatrixGenerationInputPresenter implements IMatrixGenerationInputView.Presenter {

	private IMatrixGenerationInputView view;
	private PlaceController placeController;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private String inputFile;
	
	@Inject
	public MatrixGenerationInputPresenter(IMatrixGenerationInputView view, 
			IMatrixGenerationServiceAsync matrixGenerationService,
			PlaceController placeController) {
		this.view = view;
		view.setPresenter(this);;
		this.matrixGenerationService = matrixGenerationService;
		this.placeController = placeController;
	}
	

	@Override
	public void onNext() {
		if (inputFile == null || inputFile.equals("")){
			Alerter.selectValidInputDirectory();
			return;
		}
		if (view.getTaskName() == null || view.getTaskName().equals("")){
			Alerter.selectTaskName();
			return;
		}
		
		Alerter.startLoading();
		matrixGenerationService.start(Authentication.getInstance().getToken(), 
			view.getTaskName(), inputFile, view.isInheritValues(), view.isGenerateAbsentPresent(), new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				placeController.goTo(new MatrixGenerationProcessPlace(result));
				Alerter.stopLoading();
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToStartMatrixGeneration(caught);
			}
		});
	}
				
	@Override
	public IMatrixGenerationInputView getView() {
		return view;
	}

	@Override
	public void setSelectedFolder(String fullPath, String shortendPath) {
		inputFile = fullPath;
		view.setFilePath(shortendPath);
	}
}
