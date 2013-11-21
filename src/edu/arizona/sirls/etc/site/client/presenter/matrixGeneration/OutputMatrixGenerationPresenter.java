package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.FileManagerEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.OutputMatrixGenerationView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class OutputMatrixGenerationPresenter implements OutputMatrixGenerationView.Presenter {

	private HandlerManager eventBus;
	private OutputMatrixGenerationView view;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private IMatrixGenerationServiceAsync matrixGenerationService;

	public OutputMatrixGenerationPresenter(HandlerManager eventBus, OutputMatrixGenerationView view, IFileServiceAsync fileService, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
	}

	public void go(final HasWidgets container, final Task task) {
		loadingPopup.start();
		
		view.setOutput(((MatrixGenerationConfiguration)task.getConfiguration()).getOutput());
		matrixGenerationService.output(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Void>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
			@Override
			public void onSuccess(RPCResult<Void> result) {
				//view.setOutputText(task.getConfiguration().getOutput());
				container.clear();
				container.add(view.asWidget());
				loadingPopup.stop();
			}
		});
	}

	@Override
	public void onFileManager() {
		eventBus.fireEvent(new FileManagerEvent());
	}

}
