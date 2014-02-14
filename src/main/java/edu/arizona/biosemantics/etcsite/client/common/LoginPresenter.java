package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView.ILoginListener;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class LoginPresenter implements ILoginView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private ILoginView view;
	private TitleCloseDialogBox dialogBox;
	private ILoginListener currentListener;

	@Inject
	public LoginPresenter(ILoginView view, IAuthenticationServiceAsync authenticationService) {
		this.view = view;
		view.setPresenter(this);
		this.authenticationService = authenticationService;
		this.dialogBox = new TitleCloseDialogBox(false, ""); 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
		dialogBox.setTitle("Login required");
	}

	@Override
	public void onLogin() {
		authenticationService.login(view.getUsername(), view.getPassword(), new RPCCallback<AuthenticationResult>() {
			@Override
			public void onResult(AuthenticationResult result) {
				if(result.getResult()) {
					Authentication.getInstance().setUsername(result.getUsername());
					Authentication.getInstance().setSessionID(result.getSessionID());
					dialogBox.hide();
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
		dialogBox.hide();
	}
	
	@Override
	public void show(ILoginListener listener) {
		this.currentListener = listener;
		dialogBox.center();
	}
}
