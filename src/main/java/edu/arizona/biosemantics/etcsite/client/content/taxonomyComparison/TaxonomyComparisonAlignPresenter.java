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
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationViewView;
import edu.arizona.biosemantics.etcsite.client.event.FailedTasksEvent;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.treegeneration.ITreeGenerationServiceAsync;
import edu.arizona.biosemantics.euler.alignment.client.event.model.LoadModelEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.run.EndInputVisualizationEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.run.EndMIREvent;
import edu.arizona.biosemantics.euler.alignment.client.event.run.StartInputVisualizationEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.run.StartMIREvent;
import edu.arizona.biosemantics.euler.alignment.shared.model.Model;
import edu.ucdavis.cs.cfgproject.shared.model.TaxonMatrix;

public class TaxonomyComparisonAlignPresenter implements ITaxonomyComparisonAlignView.Presenter {

	private ITaxonomyComparisonAlignView view;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private Task task;
	private Model model;
	private PlaceController placeController;
	private EventBus eulerEventBus;
	private EventBus tasksEventBus;
	
	@Inject
	public TaxonomyComparisonAlignPresenter(ITaxonomyComparisonAlignView view, 
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			PlaceController placeController, 
			@Named("Tasks") final EventBus tasksEventBus) {
		this.view = view;
		this.view.setPresenter(this);
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.placeController = placeController;
		this.tasksEventBus = tasksEventBus;
		this.eulerEventBus = view.getEulerAlignmentView().getEventBus();
		
		bindTaskEvents();
		bindEulerEvents();
	}
	
	private void bindTaskEvents() {
		tasksEventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEvent.ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(task != null && resumableTasksEvent.getTasks().containsKey(task.getId())) {
					
					/*task = resumableTasksEvent.getTasks().get(task.getId());
					if(task.getConfiguration() instanceof TaxonomyComparisonConfiguration) {
						TaxonomyComparisonConfiguration config = (TaxonomyComparisonConfiguration)task.getConfiguration();
						if(config.getOutput() != null) {
							String resultUrl = "";
							eulerEventBus.fireEvent(new EndMIREvent(resultUrl));
						}
					}*/
					task = resumableTasksEvent.getTasks().get(task.getId());
					if(task.getTaskStage() instanceof TaxonomyComparisonTaskStage) {
						TaxonomyComparisonTaskStage taskStage = (TaxonomyComparisonTaskStage)task.getTaskStage();
						TaskStageEnum taskStageEnum = taskStage.getTaskStageEnum();
						switch(taskStageEnum) {
						case INPUT:
							break;
						case ALIGN:
							break;
						case ANALYZE:
							break;
						case ANALYZE_COMPLETE:
							String resultUrl = "";
							eulerEventBus.fireEvent(new EndMIREvent(resultUrl));
							break;
						default:
							break;
						}
					}
				} else {
					
				}
			}
		});
		tasksEventBus.addHandler(FailedTasksEvent.TYPE, new FailedTasksEvent.FailedTasksEventHandler() {
			@Override
			public void onFailedTasksEvent(FailedTasksEvent failedTasksEvent) {
				if(task != null && failedTasksEvent.getTasks().containsKey(task.getId())) {
					MessageBox alert = Alerter.failedToRunTaxonomyComparison(null);
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

	private void bindEulerEvents() {
		eulerEventBus.addHandler(StartMIREvent.TYPE, new StartMIREvent.StartMIREventHandler() {
			@Override
			public void onShow(StartMIREvent event) {
				taxonomyComparisonService.runMirGeneration(Authentication.getInstance().getToken(), task, model, new AsyncCallback<Task>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToRunMirGeneration(caught);
					}
					@Override
					public void onSuccess(Task result) { 
						System.out.println("gotten result");
					}
				});
			}
		});
		eulerEventBus.addHandler(StartInputVisualizationEvent.TYPE, new StartInputVisualizationEvent.StartInputVisualizationEventHandler() {
			@Override
			public void onShow(StartInputVisualizationEvent event) {
				taxonomyComparisonService.getInputVisualization(Authentication.getInstance().getToken(), task, model, new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToRunInputVisualization(caught);
					}
					@Override
					public void onSuccess(String result) {
						eulerEventBus.fireEvent(new EndInputVisualizationEvent(result));
					}
				});
			}
		});
	}

	@Override
	public ITaxonomyComparisonAlignView getView() {
		return view;
	}

	@Override
	public void setTask(final Task task) {
		this.task = task;
		Alerter.startLoading();
		taxonomyComparisonService.getInput(Authentication.getInstance().getToken(), 
				task, new AsyncCallback<Model>() {
			@Override
			public void onSuccess(Model model) {
				TaxonomyComparisonAlignPresenter.this.model = model;
				eulerEventBus.fireEvent(new LoadModelEvent(model));
				Alerter.stopLoading();
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToLoadTaxonomies(caught);
				Alerter.stopLoading();
			}
		});
	}
}
