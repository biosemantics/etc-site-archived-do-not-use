package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.LoadingPopup;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.db.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticmarkup.TaskStageEnum;
import edu.arizona.biosemantics.otolite.client.presenter.MainPresenter;
import edu.arizona.biosemantics.otolite.client.presenter.processing.ProcessingMsgPresenter;
import edu.arizona.biosemantics.otolite.client.presenter.terminfo.TermInfoPresenter;
import edu.arizona.biosemantics.otolite.client.presenter.toontologies.ToOntologyPresenter;
import edu.arizona.biosemantics.otolite.client.view.processing.ProcessingMsgView;
import edu.arizona.biosemantics.otolite.client.view.terminfo.TermInfoView;
import edu.arizona.biosemantics.otolite.client.view.toontologies.ToOntologyView;

public class SemanticMarkupToOntologiesPresenter implements ISemanticMarkupToOntologiesView.Presenter {

	private ISemanticMarkupToOntologiesView view;
	private Task task;
	private PlaceController placeController;
	private HandlerManager eventBus;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	@Inject
	public SemanticMarkupToOntologiesPresenter(ISemanticMarkupToOntologiesView view, PlaceController placeController, ISemanticMarkupServiceAsync semanticMarkupService,
			@Named("OTOLite")HandlerManager eventBus) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.semanticMarkupService = semanticMarkupService;
		this.eventBus = eventBus;
	}
	
	@Override
	public void setTask(final Task task) {
		SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
		loadingPopup.start();
		
		this.semanticMarkupService.prepareOptionalOtoLiteSteps(Authentication.getInstance().getToken(), task, new RPCCallback<Void>() {
			@Override
			public void onResult(Void result) { 
				SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
				
				//MainPresenter mainPresenter = new MainPresenter(1, "123");
				MainPresenter mainPresenter = new MainPresenter(configuration.getOtoUploadId(), configuration.getOtoSecret());
				ToOntologyView toOntologyView = new ToOntologyView();
				toOntologyView.setSize("950px", "300px");
				ToOntologyPresenter toOntologyPresenter = new ToOntologyPresenter(toOntologyView, eventBus);
				toOntologyPresenter.go(view.getToOntologiesContainer());
				
				TermInfoView termInfoView = new TermInfoView();
				termInfoView.getTabPanel().setSize("950px", "200px");
				TermInfoPresenter termInfoPresenter = new TermInfoPresenter(termInfoView, eventBus);
				termInfoPresenter.go(view.getContextContainer());
				
				ProcessingMsgPresenter processingMsgPresenter = new ProcessingMsgPresenter(new ProcessingMsgView(), eventBus);
				processingMsgPresenter.go(null);

				SemanticMarkupToOntologiesPresenter.this.task = task;
				loadingPopup.stop();
			}
		});
	}

	@Override
	public ISemanticMarkupToOntologiesView getView() {
		return view;
	}
	
	@Override
	public void onNext() {
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.HIERARCHY, new RPCCallback<Task>() {
			@Override
			public void onResult(Task result) {
				placeController.goTo(new SemanticMarkupHierarchyPlace(task));
			}
		});
	}

}
