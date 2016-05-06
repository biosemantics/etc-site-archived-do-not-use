package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.taxonomycomparison.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.taxonomycomparison.ITaxonomyComparisonServiceAsync;
import edu.arizona.biosemantics.euler.alignment.client.event.model.LoadCollectionEvent;
import edu.arizona.biosemantics.euler.alignment.shared.model.Collection;
import edu.arizona.biosemantics.euler.alignment.shared.model.Model;

public class TaxonomyComparisonActivity extends MyAbstractActivity {

	private ITaskServiceAsync taskService;
	private ITaxonomyComparisonServiceAsync taxonomyComparisonService;
	private ITaxonomyComparisonInputView.Presenter createPresenter;
	private ITaxonomyComparisonDefineView.Presenter inputPresenter;
	private ITaxonomyComparisonAlignView.Presenter alignPresenter;
	private AcceptsOneWidget panel;
	private TaskStageEnum currentTaskStage;
	private Collection currentCollection;
	private Task currentTask;

	@Inject
	public TaxonomyComparisonActivity(ITaskServiceAsync taskService, 
			ITaxonomyComparisonServiceAsync taxonomyComparisonService,
			ITaxonomyComparisonInputView.Presenter createPresenter,
			ITaxonomyComparisonDefineView.Presenter inputPresenter,
			ITaxonomyComparisonAlignView.Presenter alignPresenter,
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.taskService = taskService;
		this.taxonomyComparisonService = taxonomyComparisonService;
		this.createPresenter = createPresenter;
		this.inputPresenter = inputPresenter;
		this.alignPresenter = alignPresenter;
		alignPresenter.getView().getEulerAlignmentView().getEventBus().addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.LoadCollectionEventHandler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				currentCollection = event.getCollection();
			}
		});
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		this.panel = panel;
		this.setStepWidget();
	}

	@Override
	public void update() {
		this.setStepWidget();
	}

	private void setStepWidget() {
		Place place = placeController.getWhere();
		if(place instanceof TaxonomyComparisonPlace) {
			currentTask = ((TaxonomyComparisonPlace)place).getTask();
		}
		if(currentTask == null){
			if(place instanceof TaxonomyComparisonDefinePlace) {
				if(createPresenter.isFromSerializedModel()) {
					inputPresenter.setSelectedSerializedModels(createPresenter.getInputTaxonomy1(), createPresenter.getInputTaxonomy2(), 
							createPresenter.getModelFolderPath1(), createPresenter.getModelFolderPath2());
				} else if(createPresenter.isFromCleantax()) {
					inputPresenter.setSelectedCleanTaxFolder(createPresenter.getCleanTaxInputFolderPath(), 
							createPresenter.getCleanTaxInputFolderShortenedPath());
				}
				panel.setWidget(inputPresenter.getView());
			} else {
				createPresenter.refresh();
				panel.setWidget(createPresenter.getView());
			}
		}
		else 
			this.taskService.getTask(Authentication.getInstance().getToken(),
					currentTask, new AsyncCallback<Task>() {
						@Override
						public void onSuccess(Task result) {
							if(result.getTaskType().getTaskTypeEnum().equals(TaskTypeEnum.TAXONOMY_COMPARISON)) {
								currentTaskStage = TaskStageEnum.valueOf(result.getTaskStage().getTaskStage());
								switch(currentTaskStage) {
								case CREATE_INPUT:
									panel.setWidget(createPresenter.getView());
									createPresenter.refresh();
									break;
								case ALIGN:
								case ANALYZE:
								case ANALYZE_COMPLETE:
									alignPresenter.setTask(result);
									panel.setWidget(alignPresenter.getView());
									break;
								case INPUT:
								default:
									panel.setWidget(inputPresenter.getView());
									break;
								}
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToGetTask(caught);
						}
			});
	}

	@Override
	public String mayStop() {
		if(currentTaskStage != null) {
			switch(currentTaskStage) {
			case ALIGN:
			case ANALYZE:
			case ANALYZE_COMPLETE:
				if(alignPresenter.hasUnsavedChanges())
					return "You have unsaved changes. Do you want to continue without saving?";
			case INPUT:
			default:
				return null;
			}
		}
		return null;
	}
	
	public void onCancel() {
		alignPresenter.setUnsavedChanges(false);
		alignPresenter.clearDialogs();
	}

	public void onStop() {
		alignPresenter.clearDialogs();
		taxonomyComparisonService.saveCollection(Authentication.getInstance().getToken(), currentTask, currentCollection, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToSaveMatrix(caught);
			}
			@Override
			public void onSuccess(Void result) {
				alignPresenter.setUnsavedChanges(false); 
			}
		});
	}
	
	
}

