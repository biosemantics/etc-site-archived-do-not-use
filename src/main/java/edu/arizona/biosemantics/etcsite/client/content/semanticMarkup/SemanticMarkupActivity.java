package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupInputView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.ITaskServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.TaskTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.TaskStageEnum;

public class SemanticMarkupActivity extends MyAbstractActivity {

	private ITaskServiceAsync taskService;
	private ISemanticMarkupInputView.Presenter inputPresenter;
	private ISemanticMarkupPreprocessView.Presenter preprocessPresenter;
	private ISemanticMarkupLearnView.Presenter learnPresenter;
	private ISemanticMarkupReviewView.Presenter reviewPresenter;
	private ISemanticMarkupToOntologiesView.Presenter toOntologyPresenter;
	private ISemanticMarkupParseView.Presenter parsePresenter;
	private ISemanticMarkupOutputView.Presenter outputPresenter;
	private AcceptsOneWidget panel;
	private Task task;
	private edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupHierarchyView.Presenter hierarchyPresenter;
	private edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.ISemanticMarkupOrdersView.Presenter ordersPresenter;

	@Inject
	public SemanticMarkupActivity(
			ITaskServiceAsync taskService,
			Presenter inputPresenter,
			ISemanticMarkupPreprocessView.Presenter preprocessPresenter,
			ISemanticMarkupLearnView.Presenter learnPresenter,
			ISemanticMarkupReviewView.Presenter reviewPresenter,
			ISemanticMarkupToOntologiesView.Presenter toOntologyPresenter,
			ISemanticMarkupHierarchyView.Presenter hierarchyPresenter,
			ISemanticMarkupOrdersView.Presenter ordersPresenter,
			ISemanticMarkupParseView.Presenter parsePresenter,
			ISemanticMarkupOutputView.Presenter outputPresenter) {
		super();
		this.taskService = taskService;
		this.inputPresenter = inputPresenter;
		this.preprocessPresenter = preprocessPresenter;
		this.learnPresenter = learnPresenter;
		this.reviewPresenter = reviewPresenter;
		this.hierarchyPresenter = hierarchyPresenter;
		this.ordersPresenter = ordersPresenter;
		this.toOntologyPresenter = toOntologyPresenter;
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

	public void setTask(Task task) {
		this.task = task;
	}

	private void setStepWidget() {
		if(task == null) 
			panel.setWidget(inputPresenter.getView());
		else 
			this.taskService.getTask(Authentication.getInstance().getToken(),
					 task, new RPCCallback<Task>() {
						@Override
						public void onResult(Task result) {
							if(result.getTaskType().getTaskTypeEnum().equals(TaskTypeEnum.SEMANTIC_MARKUP)) {
								switch(TaskStageEnum.valueOf(result.getTaskStage().getTaskStage())) {
								case INPUT:
									panel.setWidget(inputPresenter.getView());
									break;
								case OUTPUT:
									outputPresenter.setTask(task);
									panel.setWidget(outputPresenter.getView());
									break;
								case PREPROCESS_TEXT:
									preprocessPresenter.setTask(task);
									panel.setWidget(preprocessPresenter.getView());
									break;
								case LEARN_TERMS:
									learnPresenter.setTask(task);
									panel.setWidget(learnPresenter.getView());
									break;
								case REVIEW_TERMS:
									reviewPresenter.setTask(task);
									panel.setWidget(reviewPresenter.getView());
									break;
								case TO_ONTOLOGIES:
									toOntologyPresenter.setTask(task);
									panel.setWidget(toOntologyPresenter.getView());
									break;
								case HIERARCHY:
									hierarchyPresenter.setTask(task);
									panel.setWidget(hierarchyPresenter.getView());
									break;
								case ORDERS:
									ordersPresenter.setTask(task);
									panel.setWidget(ordersPresenter.getView());
									break;
								case PARSE_TEXT:
									parsePresenter.setTask(task);
									panel.setWidget(parsePresenter.getView());
									break;
								default:
									panel.setWidget(inputPresenter.getView());
									break;
								}
							}
						}
			});
	}


}
