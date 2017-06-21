package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.http.client.URL;
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
import edu.arizona.biosemantics.etcsite.client.common.MyWindow;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.client.common.files.FilePathShortener;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.event.FailedTasksEvent;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.model.MatrixGenerationConfiguration;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.matrixgeneration.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.IMatrixGenerationServiceAsync;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixFormat;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class MatrixGenerationProcessPresenter implements IMatrixGenerationProcessView.Presenter {

	private IFileServiceAsync fileService;
	private IMatrixGenerationProcessView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private Task task;
	private PlaceController placeController;
	private FilePathShortener filePathShortener;
	
	@Inject
	public MatrixGenerationProcessPresenter(final IMatrixGenerationProcessView view, 
			IMatrixGenerationServiceAsync matrixGenerationService, 
			final PlaceController placeController, 
			@Named("EtcSite") final EventBus eventBus, 
			FilePathShortener filePathShortener, 
			IFileServiceAsync fileService) {
		super();
		this.fileService = fileService;
		this.view = view;
		this.filePathShortener = filePathShortener;
		view.setPresenter(this);
		this.matrixGenerationService = matrixGenerationService;
		this.placeController = placeController;
		
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEvent.ResumableTasksEventHandler() {	
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(task != null && resumableTasksEvent.getTasks().containsKey(task.getId())) {
					view.setResumable();
				} else {
					view.setNonResumable();
				}
			}
		});
		eventBus.addHandler(FailedTasksEvent.TYPE, new FailedTasksEvent.FailedTasksEventHandler() {
			@Override
			public void onFailedTasksEvent(FailedTasksEvent failedTasksEvent) {
				if(task != null && failedTasksEvent.getTasks().containsKey(task.getId())) {
					Task failedTask = failedTasksEvent.getTasks().get(task.getId());
					TaskStageEnum failedtaskStageEnum = TaskStageEnum.valueOf(failedTask.getTaskStage().getTaskStage());
					if(failedtaskStageEnum.equals(TaskStageEnum.PROCESS)) {
						MessageBox alert;
						if (failedTask.isTooLong()){
							alert = Alerter.matrixGenerationTookTooLong(null);
						} else {
							alert = Alerter.failedToGenerateMatrix(null);
						}
						alert.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
							@Override
							public void onSelect(SelectEvent event) {
								placeController.goTo(new TaskManagerPlace());
							}
						});
					}
				}
			}
		});
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
		MatrixGenerationConfiguration config = (MatrixGenerationConfiguration) task.getConfiguration();
		view.setOutput(filePathShortener.shortenPath(config.getOutput()));
		view.setNonResumable();
		this.task = task;
		matrixGenerationService.process(Authentication.getInstance().getToken(), 
			task, new AsyncCallback<Task>() {
			@Override
			public void onSuccess(Task result) {
				MatrixGenerationProcessPresenter.this.task = result;
			}
			@Override
			public void onFailure(Throwable caught) { }
		});
	}

	@Override
	public void onReview() {
		placeController.goTo(new MatrixGenerationReviewPlace(task));
	}

	@Override
	public void onOutput() {
		final MessageBox box = Alerter.startLoading();
		matrixGenerationService.review(Authentication.getInstance().getToken(), task, new AsyncCallback<Model>() { 
			public void onSuccess(final Model model) {
				//matrixGenerationService.completeReview(Authentication.getInstance().getToken(), 
				//		task, new AsyncCallback<Task>() {
				//	@Override
				//	public void onSuccess(Task result) {
						final MyWindow window = MyWindow.open(null, "_blank", null);
						matrixGenerationService.outputMatrix(Authentication.getInstance().getToken(), 
								task, model, MatrixFormat.CSV, new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								Alerter.stopLoading(box);
								window.setUrl("download.dld?target=" + URL.encodeQueryString(result) + 
										"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
										"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()));
								/*Window.open("download.dld?target=" + URL.encodeQueryString(result) + 
										"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
										"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()), "_blank", "");
								*/
								//placeController.goTo(new MatrixGenerationOutputPlace(task));
							}
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToOutputMatrix(caught);
								Alerter.stopLoading(box);
							}
						});	
				//	}
				//	@Override
				//	public void onFailure(Throwable caught) {
				//		Alerter.failedToCompleteReview(caught);
				//		Alerter.stopLoading(box);
				//	}
				//});
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToReview(caught);
				Alerter.stopLoading(box);
			}
		});
	}
	
	@Override
	public void onOutputMC() {
		final MessageBox box = Alerter.startLoading();
		matrixGenerationService.review(Authentication.getInstance().getToken(), task, new AsyncCallback<Model>() { 
			public void onSuccess(final Model model) {
						final MyWindow window = MyWindow.open(null, "_blank", null);
						matrixGenerationService.outputMatrix(Authentication.getInstance().getToken(), 
								task, model, MatrixFormat.MCCSV, new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								Alerter.stopLoading(box);
								window.setUrl("download.dld?target=" + URL.encodeQueryString(result) + 
										"&userID=" + URL.encodeQueryString(String.valueOf(Authentication.getInstance().getUserId())) + "&" + 
										"sessionID=" + URL.encodeQueryString(Authentication.getInstance().getSessionId()));
							}
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToOutputMatrix(caught);
								Alerter.stopLoading(box);
							}
						});	
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToReview(caught);
				Alerter.stopLoading(box);
			}
		});
	}
}
