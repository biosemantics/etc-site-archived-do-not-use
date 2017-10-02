package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView.ILoginListener;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent;
import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent.AuthenticationEventType;
import edu.arizona.biosemantics.etcsite.client.layout.IEtcSiteView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;

public class LoginPresenter implements ILoginView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private ILoginView loginView;
	private ILoginListener currentListener;
	private Dialog dialog;
	private EventBus eventBus;

	@Inject
	public LoginPresenter(ILoginView loginView, IAuthenticationServiceAsync authenticationService, @Named("EtcSite")EventBus eventBus) {
		this.loginView = loginView;
		loginView.setPresenter(this);
		this.authenticationService = authenticationService;
		this.eventBus = eventBus;
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("Login");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.setPredefinedButtons();

		dialog.add(loginView.asWidget());
	}

	@Override
	public void onLogin() {
		String password = loginView.getPassword();
		authenticationService.login(loginView.getUsername(), password, new AsyncCallback<AuthenticationResult>() {
			@Override
			public void onSuccess(AuthenticationResult result) {
				if(result.getResult()) {
					Authentication auth = Authentication.getInstance();
					auth.setUserId(result.getUser().getId());
					auth.setSessionID(result.getSessionID());
					auth.setEmail(result.getUser().getEmail());
					auth.setFirstName(result.getUser().getFirstName());
					auth.setLastName(result.getUser().getLastName());
					auth.setAffiliation(result.getUser().getAffiliation());
					
					dialog.hide();
					if(currentListener != null)
						currentListener.onLogin();
					
					eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.LOGGEDIN));
				} else {
					if(currentListener != null)
						currentListener.onLoginFailure();
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToAuthenticate(caught);
			}
		});
	}
	

	@Override
	public void onCancel() {
		if (currentListener != null)
			currentListener.onCancel();
	}
	
	@Override
	public void show(ILoginListener listener) {
		loginView.clearPasswordTextBox();
		loginView.giveLoginFocus();
		this.currentListener = listener;
		dialog.show();
	}
	
	@Override
	public void setEmailField(String str){
		loginView.setEmail(str);
	}
	
	@Override
	public void setMessage(String str){
		loginView.setMessage(str);
	}

	@Override
	public void onRegisterRequest() {
		dialog.hide();
		currentListener.onRegisterRequest();
	}

	@Override
	public void onResetPasswordRequest() {
		dialog.hide();
		currentListener.onResetPasswordRequest();
	}
	
	@Override
	public void onSignInWithGoogle() {
		dialog.hide();
		String url = "https://accounts.google.com/o/oauth2/auth"
				+ "?scope=https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email"
				+ "&client_id=" + ServerSetup.getInstance().getSetup().getGoogleClientId()
				+ "&redirect_uri=" + ServerSetup.getInstance().getSetup().getGoogleRedirectURI() 
				+ "&state=" + URL.encodeQueryString(HomePlace.class.getSimpleName() + "&action=login")
				+ "&response_type=token";
		Location.replace(url);
	}

	@Override
	public String getEmailField() {
		return loginView.getUsername();
	}
}
