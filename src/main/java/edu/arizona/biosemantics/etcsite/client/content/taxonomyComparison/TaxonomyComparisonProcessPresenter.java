package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.event.FailedTasksEvent;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.MatrixGenerationException;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonService;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;

public class TaxonomyComparisonProcessPresenter implements ITaxonomyComparisonProcessView.Presenter {

	private ITaxonomyComparisonProcessView view;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private Task task;
	private PlaceController placeController;
	
	@Inject
	public TaxonomyComparisonProcessPresenter(final ITaxonomyComparisonProcessView view, 
			ITaxonomyComparisonServiceAsync taxonomyComparisonService, 
			final PlaceController placeController, 
			@Named("Tasks") final EventBus tasksBus) {
		super();
		this.view = view;
		view.setPresenter(this);
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.placeController = placeController;
		
		tasksBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEvent.ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(task != null && resumableTasksEvent.getTasks().containsKey(task.getId())) {
					view.setResumable();
				} else {
					view.setNonResumable();
				}
			}
		});
		tasksBus.addHandler(FailedTasksEvent.TYPE, new FailedTasksEvent.FailedTasksEventHandler() {
			@Override
			public void onFailedTasksEvent(FailedTasksEvent failedTasksEvent) {
				if(task != null && failedTasksEvent.getTasks().containsKey(task.getId())) {
					MessageBox alert = Alerter.failedToGenerateMatrix(null);
					alert.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							placeController.goTo(new TaskManagerPlace());
						}
					});
				}
			}
		});
	}

	@Override
	public void onNext() {
		placeController.goTo(new TaxonomyComparisonViewPlace(task));
	}

	@Override
	public void onTaskManager() {
		placeController.goTo(new TaskManagerPlace());
	}

	@Override
	public ITaxonomyComparisonProcessView getView() {
		return view;
	}

	@Override
	public void setTask(Task task) {
		this.task = task;
		view.setNonResumable();
		taxonomyComparisonService.process(Authentication.getInstance().getToken(), 
			task, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				TaxonomyComparisonProcessPresenter.this.task = result;
			}
			@Override
			public void onFailure(Throwable caught) { }
		});
	}
}
