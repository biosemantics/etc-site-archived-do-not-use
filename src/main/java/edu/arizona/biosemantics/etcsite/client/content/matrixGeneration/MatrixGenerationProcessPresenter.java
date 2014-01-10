package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.ResumableTasksEventHandler;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class MatrixGenerationProcessPresenter implements IMatrixGenerationProcessView.Presenter {

	private IMatrixGenerationProcessView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private Task task;
	private PlaceController placeController;
	
	@Inject
	public MatrixGenerationProcessPresenter(final IMatrixGenerationProcessView view, 
			IMatrixGenerationServiceAsync matrixGenerationService, 
			PlaceController placeController, 
			@Named("Tasks") final EventBus tasksBus) {
		super();
		this.view = view;
		view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
		this.placeController = placeController;
		
		tasksBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(task != null && resumableTasksEvent.getTasks().containsKey(task.getId())) {
					view.setResumable();
				} else {
					view.setNonResumable();
				}
			}
		});
	}

	@Override
	public void onNext() {
		placeController.goTo(new MatrixGenerationReviewPlace(task));
	}

	@Override
	public void onTaskManager() {
		placeController.goTo(new TaskManagerPlace());
	}

	@Override
	public IMatrixGenerationProcessView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		this.task = task;
		matrixGenerationService.process(Authentication.getInstance().getToken(), 
			task, new RPCCallback<Task>() {
			@Override
			public void onResult(Task result) {
				MatrixGenerationProcessPresenter.this.task = result;
			}
		});
	}
}
