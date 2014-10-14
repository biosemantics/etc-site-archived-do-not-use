package edu.arizona.biosemantics.etcsite.client.content.taskManager;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;

public class TaskManagerActivity extends MyAbstractActivity { 

	private ITaskManagerView.Presenter taskManagerPresenter;

	@Inject
	public TaskManagerActivity(ITaskManagerView.Presenter taskManagerPresenter, 
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter,
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
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
