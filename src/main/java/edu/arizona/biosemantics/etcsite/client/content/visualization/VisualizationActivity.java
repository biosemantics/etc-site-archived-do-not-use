package edu.arizona.biosemantics.etcsite.client.content.visualization;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import edu.arizona.biosemantics.etcsite.client.common.HasTaskPlace;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;

public class VisualizationActivity extends MyAbstractActivity {

	public VisualizationActivity(PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter){
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		// TODO Auto-generated method stub
		
	}

	public HasTaskPlace setTask(VisualizationPlace place) {
		// TODO Auto-generated method stub
		return null;
	}

}
