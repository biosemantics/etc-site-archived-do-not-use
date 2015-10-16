package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.content.gettingstarted.GettingStartedPlace;
import edu.arizona.biosemantics.etcsite.client.help.ICompleteHelpView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;


public class HelpActivity extends MyAbstractActivity implements Presenter {

		private ICompleteHelpView completeHelpView;

		@Inject
		public HelpActivity(ICompleteHelpView completeHelpView, PlaceController placeController, 
				IAuthenticationServiceAsync authenticationService, 
				ILoginView.Presenter loginPresenter, 
				IRegisterView.Presenter registerPresenter,
				IResetPasswordView.Presenter resetPasswordPresenter){
			super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
			this.completeHelpView = completeHelpView;
		}
		
		@Override
		public void start(AcceptsOneWidget panel, EventBus eventBus) {
			completeHelpView.setPresenter(this);
			panel.setWidget(completeHelpView.asWidget());
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



