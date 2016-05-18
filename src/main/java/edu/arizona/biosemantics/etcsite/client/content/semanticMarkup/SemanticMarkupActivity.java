package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

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
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupDefineView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.model.TaskTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;
import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
@SuppressWarnings("unused")
public class SemanticMarkupActivity extends MyAbstractActivity {

	private ITaskServiceAsync taskService;
	private ISemanticMarkupInputView.Presenter createPresenter;
	private ISemanticMarkupDefineView.Presenter inputPresenter;
	private ISemanticMarkupPreprocessView.Presenter preprocessPresenter;
	private ISemanticMarkupLearnView.Presenter learnPresenter;
	private ISemanticMarkupReviewView.Presenter reviewPresenter;
	private ISemanticMarkupParseView.Presenter parsePresenter;
	private ISemanticMarkupOutputView.Presenter outputPresenter;
	private TaskStageEnum currentTaskStage;
	private AcceptsOneWidget panel;

	@Inject
	public SemanticMarkupActivity(
			ITaskServiceAsync taskService,
			Presenter inputPresenter,
			ISemanticMarkupInputView.Presenter createPresenter,
			ISemanticMarkupPreprocessView.Presenter preprocessPresenter,
			ISemanticMarkupLearnView.Presenter learnPresenter,
			ISemanticMarkupReviewView.Presenter reviewPresenter,
			ISemanticMarkupParseView.Presenter parsePresenter,
			ISemanticMarkupOutputView.Presenter outputPresenter, 
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.taskService = taskService;
		this.createPresenter = createPresenter;
		this.inputPresenter = inputPresenter;
		this.preprocessPresenter = preprocessPresenter;
		this.learnPresenter = learnPresenter;
		this.reviewPresenter = reviewPresenter;
		this.parsePresenter = parsePresenter;
		this.outputPresenter = outputPresenter;
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
		Task task = null;
		Place place = placeController.getWhere();
		if(place instanceof SemanticMarkupPlace)
			task = ((SemanticMarkupPlace)place).getTask();
		if(task == null){
			//TODO: normally the info of type input folder is tied to InputPlace and should reside there, tokenization, linking, etc.
			if(place instanceof SemanticMarkupDefinePlace){
				inputPresenter.setSelectedFolder(createPresenter.getInputFolderPath(), createPresenter.getInputFolderShortenedPath());
				panel.setWidget(inputPresenter.getView());
			}else{
				createPresenter.refresh();
				panel.setWidget(createPresenter.getView());
			}
		} else 
			this.taskService.getTask(Authentication.getInstance().getToken(),
					 task, new AsyncCallback<Task>() {
						@Override
						public void onSuccess(Task result) {
							if(result.getTaskType().getTaskTypeEnum().equals(TaskTypeEnum.SEMANTIC_MARKUP)) {
								currentTaskStage = TaskStageEnum.valueOf(result.getTaskStage().getTaskStage());
								switch(currentTaskStage) {
								case CREATE_INPUT:
									panel.setWidget(createPresenter.getView());
									createPresenter.refresh();
									break;
								case INPUT:
									inputPresenter.setSelectedFolder(createPresenter.getInputFolderPath(), createPresenter.getInputFolderShortenedPath());
									panel.setWidget(inputPresenter.getView());
									break;
								case OUTPUT:
									outputPresenter.setTask(result);
									panel.setWidget(outputPresenter.getView());
									break;
								case PREPROCESS_TEXT:
									preprocessPresenter.setTask(result);
									panel.setWidget(preprocessPresenter.getView());
									break;
								case LEARN_TERMS:
									learnPresenter.setTask(result);
									panel.setWidget(learnPresenter.getView());
									break;
								case REVIEW_TERMS:
									reviewPresenter.setTask(result);
									panel.setWidget(reviewPresenter.getView());
									break;
								case PARSE_TEXT:
									parsePresenter.setTask(result);
									panel.setWidget(parsePresenter.getView());
									break;
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
			case CREATE_INPUT:
				break;
			case INPUT:
				break;
			case LEARN_TERMS:
				break;
			case OUTPUT:
				break;
			case PARSE_TEXT:
				break;
			case PREPROCESS_TEXT:
				break;
			case REVIEW_TERMS:
				if(reviewPresenter.hasUnsavedChanges()) {
					return "You have unsaved changes. Do you want to continue without saving?";
				}
				onStop();
				break;
			default:
				break;
			}
		}
		return null;
	}
		
	@Override
	public void onStop() {
		reviewPresenter.removeSaveTimer();
	}
	

}