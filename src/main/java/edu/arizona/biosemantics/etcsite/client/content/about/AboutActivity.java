package edu.arizona.biosemantics.etcsite.client.content.about;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.content.about.IAboutView.Presenter;
import edu.arizona.biosemantics.etcsite.client.content.gettingstarted.GettingStartedPlace;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;


public class AboutActivity extends MyAbstractActivity implements Presenter {

		private IAboutView aboutView;

		@Inject
		public AboutActivity(IAboutView aboutView,  PlaceController placeController, 
				IAuthenticationServiceAsync authenticationService, 
				ILoginView.Presenter loginPresenter, 
				IRegisterView.Presenter registerPresenter,
				IResetPasswordView.Presenter resetPasswordPresenter){
			super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
			this.aboutView = aboutView;
			
		}
		
		@Override
		public void start(AcceptsOneWidget panel, EventBus eventBus) {
			aboutView.setPresenter(this);
			panel.setWidget(aboutView.asWidget());
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetStart() {
			placeController.goTo(new GettingStartedPlace());
		}

	}



