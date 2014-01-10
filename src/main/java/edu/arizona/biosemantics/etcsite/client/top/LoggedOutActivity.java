package edu.arizona.biosemantics.etcsite.client.top;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.help.HelpPlace;
import edu.arizona.biosemantics.etcsite.client.top.ILoginTopView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class LoggedOutActivity extends AbstractActivity implements Presenter {

	private PlaceController placeController;
	private ILoginTopView loginTopView;
	private IAuthenticationServiceAsync authenticationService;

	@Inject
	public LoggedOutActivity(ILoginTopView loginTopView, PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService) {
		this.loginTopView = loginTopView;
		this.placeController = placeController;
		this.authenticationService = authenticationService;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		Authentication authentication = Authentication.getInstance();
		if(authentication.isSet()) {
			authenticationService.isValidSession(authentication.getToken(), new RPCCallback<AuthenticationResult>() {
				@Override
				public void onResult(AuthenticationResult result) {
					if(result.getResult()) {
						placeController.goTo(new LoggedInPlace());
					} else {
						setLoginView(panel);
					}
				}
			});
		} else {
			setLoginView(panel);
		}
	}

	private void setLoginView(AcceptsOneWidget panel) {
		loginTopView.setPresenter(this);
		panel.setWidget(loginTopView.asWidget());
	}

	@Override
	public void onHelp() {
		placeController.goTo(new HelpPlace());
	}

	@Override
	public void onLogin() {
		String username = loginTopView.getUser();
		String password = loginTopView.getPassword();
		authenticationService.login(username, password, new RPCCallback<AuthenticationResult>() {
			@Override
			public void onResult(AuthenticationResult result) {
				if(result.getResult()) {
					Authentication.getInstance().setUsername(result.getUsername());
					Authentication.getInstance().setSessionID(result.getSessionID());
					placeController.goTo(new LoggedInPlace());
				}
			}
		});
	}
}
