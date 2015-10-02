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
import edu.arizona.biosemantics.euler.alignment.client.event.model.SetColorEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.SetColorsEvent;
import edu.arizona.biosemantics.euler.alignment.client.event.model.SetCommentEvent;
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
		    
		    // can't make modal, otherwise user wno't be able to go to task/file/manager and the like
		    //this.setModal(true);
		    //this.setBlinkModal(true);
		    
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
			@Named("EtcSite") final EventBus tasksEventBus, 
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
												possibleWorld.setUrl(getAuthenticatedGetPDFUrl(possibleWorld.getUrl()));
											}
											RunOutput output = new RunOutput(result.getType(), result.getPossibleWorlds(), getAuthenticatedGetPDFUrl(result.getAggregateUrl()), 
													getAuthenticatedGetPDFUrl(result.getDiagnosisUrl()));
											eulerEventBus.fireEvent(new EndMIREvent(output));
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
					Task failedTask = failedTasksEvent.getTasks().get(task.getId());
					onSave();
					TaskStageEnum failedTaskStageEnum = TaskStageEnum.valueOf(failedTask.getTaskStage().getTaskStage());
					//if(failedTaskStageEnum.equals(TaskStageEnum.ANALYZE)) {
						MessageBox alert;
						if (failedTask.isTooLong()){
							alert = Alerter.taxonomyComparisonTookTooLong(null);
						} else {
							alert = Alerter.failedToRunTaxonomyComparison(null);
						}
						alert.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
							@Override
							public void onSelect(SelectEvent event) {
								placeController.goTo(new TaskManagerPlace());
							}
						});
					//}
				}
			}
		});
	}
	
	private String getAuthenticatedGetPDFUrl(String url) {
		return "result.gpdf?target=" + URL.encodeQueryString(url) + 
			"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
			"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId());
	}

	private void bindEulerEvents() {
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
		eulerEventBus.addHandler(SetColorEvent.TYPE, new SetColorEvent.SetColorEventHandler() {
			@Override
			public void onSet(SetColorEvent event) {
				unsavedChanges = true;
			}
		});
		eulerEventBus.addHandler(SetCommentEvent.TYPE, new SetCommentEvent.SetCommentEventHandler() {
			
			@Override
			public void onSet(SetCommentEvent event) {
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
		eulerEventBus.addHandler(EndMIREvent.TYPE, new EndMIREvent.EndMIREventHandler() {
			@Override
			public void onEnd(EndMIREvent event) {
				unsavedChanges = true;
				taxonomyComparisonService.saveModel(Authentication.getInstance().getToken(), task, model, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToSaveTaxonomyComparisonModel(caught);
					}
					@Override
					public void onSuccess(Void result) { 
						//unsavedChanges = false;
					}
				});
			}
		});
		eulerEventBus.addHandler(StartMIREvent.TYPE, new StartMIREvent.StartMIREventHandler() {
			@Override
			public void onShow(StartMIREvent event) {
				unsavedChanges = true;
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
						result = "result.gpdf?target=" + URL.encodeQueryString(result) + 
								"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
								"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId());
						eulerEventBus.fireEvent(new EndInputVisualizationEvent(result));
					}
				});
			}
		});
		eulerEventBus.addHandler(SaveEvent.TYPE, new SaveEvent.SaveHandler() {
			@Override
			public void onSave(SaveEvent event) {
				TaxonomyComparisonAlignPresenter.this.onSave();
			}
		});
		eulerEventBus.addHandler(DownloadEvent.TYPE, new DownloadEvent.DownloadHandler() {
			@Override
			public void onDownload(DownloadEvent event) {
				final MessageBox box = Alerter.startLoading();
				taxonomyComparisonService.exportArticulations(Authentication.getInstance().getToken(), 
						task, event.getModel(), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						Alerter.stopLoading(box);
						Window.open("download.dld?target=" + URL.encodeQueryString(result) + 
								"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
								"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()), "_blank", "");
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToOutputMatrix(caught);
						Alerter.stopLoading(box);
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
		final MessageBox box = Alerter.startLoading();
		view.getEulerAlignmentView().setShowDialogs(true);
		taxonomyComparisonService.getModel(Authentication.getInstance().getToken(), 
				task, new AsyncCallback<Model>() {
			@Override
			public void onSuccess(Model model) {
				TaxonomyComparisonAlignPresenter.this.model = model;
				eulerEventBus.fireEvent(new LoadModelEvent(model));
				TaskStageEnum currentTaskStage = TaskStageEnum.valueOf(task.getTaskStage().getTaskStage());
				switch(currentTaskStage) {
				case ALIGN:
					break;
				case ANALYZE:
					processingDialog.show();
					break;
				case ANALYZE_COMPLETE:	
					break;
				}
				Alerter.stopLoading(box);
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToLoadTaxonomies(caught);
				Alerter.stopLoading(box);
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

	@Override
	public void clearDialogs() {
		processingDialog.hide();
		view.getEulerAlignmentView().setShowDialogs(false);
	}

	@Override
	public void onSave() {
		final MessageBox box = Alerter.startLoading();
		taxonomyComparisonService.saveModel(Authentication.getInstance().getToken(), task, model, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSaveTaxonomyComparisonModel(caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(Void result) { 
				Alerter.stopLoading(box);
				unsavedChanges = false;
			}
		});	
	}
}
