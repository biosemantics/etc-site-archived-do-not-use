package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView.ILoginListener;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class LoginPresenter implements ILoginView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private ILoginView loginView;
	private PopupPanel loginPopup;
	private ILoginListener currentListener;

	@Inject
	public LoginPresenter(ILoginView loginView, IAuthenticationServiceAsync authenticationService) {
		this.loginView = loginView;
		loginView.setPresenter(this);
		this.authenticationService = authenticationService;
		loginPopup = new PopupPanel(true){
			@Override
			public void hide(boolean autoClosed){
				if (autoClosed)
					onCancel();
				super.hide(autoClosed);
			}
		}; //true means that the popup will close when the user clicks outside of it.
		loginPopup.setGlassEnabled(true);
		loginPopup.add(loginView.asWidget());
	}

	@Override
	public void onLogin() {
		String password = loginView.getPassword();
		authenticationService.login(loginView.getUsername(), password, new RPCCallback<AuthenticationResult>() {
			@Override
			public void onResult(AuthenticationResult result) {
				if(result.getResult()) {
					Authentication auth = Authentication.getInstance();
					auth.setUserId(result.getUserId());
					auth.setSessionID(result.getSessionID());
					
					loginPopup.hide();
					if(currentListener != null)
						currentListener.onLogin();
				} else {
					if(currentListener != null)
						currentListener.onLoginFailure();
				}
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
		loginPopup.center();
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
		loginPopup.hide();
		currentListener.onRegisterRequest();
	}

	@Override
	public void onResetPasswordRequest() {
		loginPopup.hide();
		currentListener.onResetPasswordRequest();
	}

	@Override
	public String getEmailField() {
		return loginView.getUsername();
	}
	

}