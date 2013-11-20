package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.InputMatrixGenerationView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.OutputMatrixGenerationView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;

public class OutputMatrixGenerationPresenter implements OutputMatrixGenerationView.Presenter {

	private HandlerManager eventBus;
	private OutputMatrixGenerationView view;

	public OutputMatrixGenerationPresenter(HandlerManager eventBus, OutputMatrixGenerationView view, IFileServiceAsync fileService, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
	}

	public void go(HasWidgets container, MatrixGenerationTaskRun matrixGenerationTaskRun) {
		container.clear();
		container.add(view.asWidget());
	}

}
