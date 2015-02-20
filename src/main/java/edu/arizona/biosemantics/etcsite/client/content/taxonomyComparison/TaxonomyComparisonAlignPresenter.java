package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IProcessingView.IOnTaskManagerListener;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.IProcessingView.Presenter;
import edu.arizona.biosemantics.etcsite.client.event.FailedTasksEvent;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaxonomyComparisonTaskStage;
import edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.euler.alignment.client.event.DownloadEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.SaveEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.AddArticulationsEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.ImportArticulationsEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.LoadModelEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.ModifyArticulationEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.RemoveArticulationsEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.SetArticulationColorEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.SetArticulationCommentEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.SetColorsEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.SetTaxonColorEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.SetTaxonCommentEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.run.EndInputVisualizationEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.run.EndMIREvent;
import edu.arizona.biosemantics.euler.alignment.client.event.run.StartInputVisualizationEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.run.StartMIREvent;
import edu.arizona.biosemantics.euler.alignment.shared.model.Model;
import edu.arizona.biosemantics.euler.alignment.shared.model.PossibleWorld;
import edu.arizona.biosemantics.euler.alignment.shared.model.RunOutput;

public class TaxonomyComparisonAlignPresenter implements ITaxonomyComparisonAlignView.Presenter {
	
	public class ProcessingDialog extends Dialog implements IOnTaskManagerListener {
		private IProcessingView processingView;

		public ProcessingDialog(IProcessingView.Presenter processingPresenter) {
			this.processingView = processingPresenter.getView();
			processingPresenter.addOnTaskManagerListener(this);
			setBodyBorder(false);
			setHeadingText("Please wait...");
			setPixelSize(-1, -1);
			setMinWidth(0);
			setMinHeight(0);
		    setResizable(true);
		    setShadow(true);
			setHideOnButtonClick(true);
			setPredefinedButtons();
			add(processingView.asWidget());
		}

		@Override
		public void onTaskManager() {
			this.hide();
		}
	}
	
	
	private ITaxonomyComparisonAlignView view;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private Task task;
	private Model model;
	private PlaceController placeController;
	private EventBus eulerEventBus;
	private EventBus tasksEventBus;
	private Presenter processingPresenter;
	private ProcessingDialog processingDialog;
	private boolean unsavedChanges = false;
	
	@Inject
	public TaxonomyComparisonAlignPresenter(ITaxonomyComparisonAlignView view, 
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			PlaceController placeController, 
			@Named("Tasks") final EventBus tasksEventBus, 
			IProcessingView.Presenter processingPresenter
			) {
		this.view = view;
		this.view.setPresenter(this);
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.placeController = placeController;
		this.tasksEventBus = tasksEventBus;
		this.eulerEventBus = view.getEulerAlignmentView().getEventBus();
		this.processingDialog = new ProcessingDialog(processingPresenter);
		
		bindTaskEvents();
		bindEulerEvents();
	}
	
	private void bindTaskEvents() {
		tasksEventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEvent.ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(task != null && resumableTasksEvent.getTasks().containsKey(task.getId())) {
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
							taxonomyComparisonService.getMirGenerationResult(
									Authentication.getInstance().getToken(), task, new AsyncCallback<RunOutput>() {
										@Override
										public void onFailure(Throwable t) {
											processingDialog.hide();
											Alerter.failedToGetTaxonomyComparisonResult(t);
										}
										@Override
										public void onSuccess(RunOutput result) {
											processingDialog.hide();
											for(PossibleWorld possibleWorld : result.getPossibleWorlds()) {
												String oldUrl = possibleWorld.getUrl();
												possibleWorld.setUrl("result.gpdf?target=" + URL.encodeQueryString(oldUrl) + 
														"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
														"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()));
											}
											eulerEventBus.fireEvent(new EndMIREvent(result));
										}
									});
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
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(EndMIREvent.TYPE, new EndMIREvent.EndMIREventHandler() {
			
			@Override
			public void onEnd(EndMIREvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(AddArticulationsEvent.TYPE, new AddArticulationsEvent.AddArticulationEventHandler() {
			@Override
			public void onAdd(AddArticulationsEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(ModifyArticulationEvent.TYPE, new ModifyArticulationEvent.ModifyArticulationEventHandler() {
			@Override
			public void onModify(ModifyArticulationEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(SetArticulationColorEvent.TYPE, new SetArticulationColorEvent.SetArticulationColorEventHandler() {
			@Override
			public void onSet(SetArticulationColorEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(SetArticulationCommentEvent.TYPE, new SetArticulationCommentEvent.SetArticulationCommentEventHandler() {
			@Override
			public void onSet(SetArticulationCommentEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(SetTaxonCommentEvent.TYPE, new SetTaxonCommentEvent.SetTaxonCommentEventHandler() {
			@Override
			public void onSet(SetTaxonCommentEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(SetTaxonColorEvent.TYPE, new SetTaxonColorEvent.SetTaxonColorEventHandler() {
			@Override
			public void onSet(SetTaxonColorEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(SetColorsEvent.TYPE, new SetColorsEvent.SetColorsEventHandler() {
			@Override
			public void onSet(SetColorsEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(RemoveArticulationsEvent.TYPE, new RemoveArticulationsEvent.RemoveArticulationsEventHandler() {
			@Override
			public void onRemove(RemoveArticulationsEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(ImportArticulationsEvent.TYPE, new ImportArticulationsEvent.ImportArticulationsEventHandler() {
			@Override
			public void onImport(ImportArticulationsEvent event) {
				unsavedChanges = true;
			}
		});
		
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
						processingDialog.show();
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
		eulerEventBus.addHandler(SaveEvent.TYPE, new SaveEvent.SaveHandler() {
			@Override
			public void onSave(SaveEvent event) {
				Alerter.startLoading();
				taxonomyComparisonService.saveModel(Authentication.getInstance().getToken(), task, model, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToSaveTaxonomyComparisonModel(caught);
						Alerter.stopLoading();
					}
					@Override
					public void onSuccess(Void result) { 
						Alerter.stopLoading();
						unsavedChanges = false;
					}
				});
			}
		});
		eulerEventBus.addHandler(DownloadEvent.TYPE, new DownloadEvent.DownloadHandler() {
			@Override
			public void onDownload(DownloadEvent event) {
				Alerter.startLoading();
				taxonomyComparisonService.exportArticulations(Authentication.getInstance().getToken(), 
						task, event.getModel(), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						Alerter.stopLoading();
						Window.open("download.dld?target=" + URL.encodeQueryString(result) + 
								"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
								"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()), "_blank", "");
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToOutputMatrix(caught);
						Alerter.stopLoading();
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
		taxonomyComparisonService.getModel(Authentication.getInstance().getToken(), 
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
	
	@Override
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	@Override
	public void setUnsavedChanges(boolean value) {
		this.unsavedChanges = value;
	}
}
