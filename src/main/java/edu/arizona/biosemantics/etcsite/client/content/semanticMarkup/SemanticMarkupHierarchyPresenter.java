package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.db.SemanticMarkupConfiguration;
import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.ISemanticMarkupServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.semanticMarkup.TaskStageEnum;
import edu.arizona.biosemantics.otolite.client.presenter.MainPresenter;
import edu.arizona.biosemantics.otolite.client.presenter.hierarchy.HierarchyPagePresenter;
import edu.arizona.biosemantics.otolite.client.presenter.toontologies.ToOntologyPresenter;
import edu.arizona.biosemantics.otolite.client.view.hierarchy.HierarchyPageView;
import edu.arizona.biosemantics.otolite.client.view.toontologies.ToOntologyView;

public class SemanticMarkupHierarchyPresenter implements ISemanticMarkupHierarchyView.Presenter {

	private ISemanticMarkupHierarchyView view;
	private PlaceController placeController;
	private Task task;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private HandlerManager eventBus;

	@Inject
	public SemanticMarkupHierarchyPresenter(ISemanticMarkupHierarchyView view, PlaceController placeController, @Named("OTOLite")HandlerManager eventBus, 
			ISemanticMarkupServiceAsync semanticMarkupService) {
		this.view = view;
		view.setPresenter(this);
		this.placeController = placeController;
		this.eventBus = eventBus;
		this.semanticMarkupService = semanticMarkupService;
	}
	
	@Override
	public void setTask(Task task) {
		SemanticMarkupConfiguration configuration = (SemanticMarkupConfiguration)task.getConfiguration();
		
		MainPresenter mainPresenter = new MainPresenter(1, "123");
		//MainPresenter mainPresenter = new MainPresenter(configuration.getOtoUploadId(), configuration.getOtoSecret());
		
		HierarchyPageView hierarchyPageView = new HierarchyPageView();
		hierarchyPageView.setSize("950px", "500px");
		HierarchyPagePresenter hierarchyPagePresenter = new HierarchyPagePresenter(hierarchyPageView, eventBus);
		hierarchyPagePresenter.go(view.getHasWidgets());

		SemanticMarkupHierarchyPresenter.this.task = task;
	}

	@Override
	public void onNext() {
		semanticMarkupService.goToTaskStage(Authentication.getInstance().getToken(), task, TaskStageEnum.ORDERS, new RPCCallback<Task>() {
			@Override
			public void onResult(Task result) {
				placeController.goTo(new SemanticMarkupOrdersPlace(task));
			}
		});
	}
	
	@Override
	public ISemanticMarkupHierarchyView getView() {
		return view;
	}
	
}
