package edu.arizona.biosemantics.etcsite.client.content.taskManager;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

public class TaskManagerActivity extends MyAbstractActivity { 

	private ITaskManagerView.Presenter taskManagerPresenter;
	private PlaceController placeController;

	@Inject
	public TaskManagerActivity(ITaskManagerView.Presenter taskManagerPresenter, PlaceController placeController) {
		this.placeController = placeController;
		this.taskManagerPresenter = taskManagerPresenter;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		taskManagerPresenter.refresh();
		panel.setWidget(taskManagerPresenter.getView());
	}


	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
