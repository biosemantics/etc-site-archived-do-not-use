package edu.arizona.biosemantics.etcsite.client.content.sample;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.core.shared.rpc.user.IUserServiceAsync;

public class SampleActivity extends MyAbstractActivity implements ISampleView.Presenter {

	private IUserServiceAsync userService;
	private ISampleView view;

	@Inject
	public SampleActivity(ISampleView sampleView, 
			PlaceController placeController,
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter, 
			IUserServiceAsync userService) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.view = sampleView;
		this.userService = userService;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Place place = placeController.getWhere();
		if(place instanceof SamplePlace) {
			SamplePlace samplePlace = ((SamplePlace)place);
		}
		view.setPresenter(this);
		panel.setWidget(view.asWidget());
	}
	
	@Override
	public void update() {}

}
