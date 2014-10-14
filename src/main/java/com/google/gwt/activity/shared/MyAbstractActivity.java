package com.google.gwt.activity.shared;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView.ILoginListener;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView.IRegisterListener;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView.IResetPasswordListener;
import edu.arizona.biosemantics.etcsite.client.top.LoggedInPlace;
import edu.arizona.biosemantics.etcsite.shared.log.LogLevel;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;

public abstract class MyAbstractActivity implements MyActivity {
	
	protected PlaceController placeController;
	protected IAuthenticationServiceAsync authenticationService;
	private ILoginView.Presenter loginPresenter;
	private IRegisterView.Presenter registerPresenter;
	private IResetPasswordView.Presenter resetPasswordPresenter;
	
	public MyAbstractActivity(PlaceController placeController, IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter) {
		this.placeController = placeController;
		this.authenticationService = authenticationService;
		this.loginPresenter = loginPresenter;
		this.registerPresenter = registerPresenter;
		this.resetPasswordPresenter = resetPasswordPresenter;
	}
	
	/**
	 * If any Activity extending MyAbstractActivity requires the user to be logged in before proceeding, this method 
	 * simplifies the process. 
	 * 
	 * 	-If the user is not logged in, shows the login window, giving the user a chance to log in/register an account.
	 * 	-When the user logs in, the caller is notified via the LoggedInListener's onLoggedIn() method.  
	 * 	-If the user cancels login, the caller is notified via the LoggedInListener's onCancel() method.
	 * 	-If the user is already logged in, no window is shown and onLoggedIn() is called immediately.
	 *
	 * Usage example: 
	 *
	 * //Requires the user to be logged in before beginning processing. 
	 * 
	 * public class ProcessingActivity extends MyAbstractActivity {
	 * 
	 * 	...
	 * 
	 * 	private void tryBeginProcessing() {
	 * 
	 * 		this.requireLogin(new LoggedInListener() {
	 * 			@Override
	 * 			public void onLoggedIn() {
	 * 				//when the user is logged in, begin processing. 
	 * 				beginProcessing();
	 * 			}
	 * 
	 * 			@Override
	 * 			public void onCancel() {
	 * 				//if the user cancels login, show an error message. 
	 * 				showMessage("You must login to begin processing.");
	 * 			}
	 * 		});
	 * 	}
	 * 
	 * } 
	 */

	protected void requireLogin(final LoggedInListener listener){
		Authentication authentication = Authentication.getInstance();
		if(authentication.isSet()) {
			authenticationService.isValidSession(authentication.getToken(), new AsyncCallback<AuthenticationResult>() {
				@Override
				public void onSuccess(AuthenticationResult result) {
					if(result.getResult()) { //user is already logged in.
						
						listener.onLoggedIn(); //notify the listener that the user is logged in; they may proceed. 
						
					} else { //there was a user logged in, but it is no longer a valid session. 
						Authentication.getInstance().destroy(); //clear the invalid session cookies.
						
						//give the user a chance to log in. 
						showLoginWindow("Your session has expired. Please log in again.", new LoggedInListener() {
							
							@Override
							public void onLoggedIn() {
								listener.onLoggedIn(); //notify the caller that the user is logged in; they may proceed. 
							}
							@Override
							public void onCancel() {
								listener.onCancel(); //notify the caller that the user canceled login. 
							}
						});
					}
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToAuthenticate(caught);
				}
			});
		} else {
			showLoginWindow("You must be signed in to use this feature.", new LoggedInListener() {
				
				@Override
				public void onLoggedIn() {
					listener.onLoggedIn(); //notify the caller that the user is logged in; they may proceed. 
				}

				@Override
				public void onCancel() {
					listener.onCancel(); //notify the caller that the user canceled login. 
				}
			});
		}
	}
	
	protected void showLoginWindow(){ //use if the caller does not need to be notified on login, login failure, or register request.
		showLoginWindow(null, new LoggedInListener(){ //call with a dummy logged in listener. 
			@Override
			public void onLoggedIn() {}
			@Override
			public void onCancel() {} 
		});
	}
	
	protected void showLoginWindow(String message){ //use if the caller does not need to be notified on login, login failure, or register request.
		showLoginWindow(message, new LoggedInListener(){ //call with a dummy logged in listener. 
			@Override
			public void onLoggedIn() {}
			@Override
			public void onCancel() {} 
		});
	}
	
	protected void showLoginWindow(final LoggedInListener listener){ //use if the caller needs to be notified on login, login failure, or register request.
		showLoginWindow(null, listener);
	}
	
	protected void showLoginWindow(String message, final LoggedInListener listener){ //use if the caller needs to be notified on login, login failure, or register request.
		if (message == null)
			message = "Enter sign-in credentials below.";
			
		loginPresenter.setMessage(message);
		loginPresenter.show(new ILoginListener() {
			@Override
			public void onLogin() {
				placeController.goTo(new LoggedInPlace());
				listener.onLoggedIn(); //let the caller 'go to' a different place. 
			}
			@Override
			public void onLoginFailure() {
				loginPresenter.setMessage("Invalid sign-in. Please try again.");
				loginPresenter.show(this);
			}
			@Override
			public void onRegisterRequest() {
				showRegisterWindow(listener);
			}
			@Override
			public void onResetPasswordRequest() {
				showResetPasswordWindow(listener);
			}
			
			@Override 
			public void onCancel(){
				listener.onCancel();
			}
		});
	}
	
	public void showRegisterWindow(){
		showRegisterWindow(new LoggedInListener(){
			@Override
			public void onLoggedIn() {}
			@Override
			public void onCancel() {}
		});
	}
	
	private void showRegisterWindow(final LoggedInListener listener){
		registerPresenter.show(new IRegisterListener() {
			@Override
			public void onRegister() {
				loginPresenter.setMessage("Your account has been registered!");
				loginPresenter.setEmailField(registerPresenter.getEmail());
				registerPresenter.clearAllBoxes();
				showLoginWindow("Your account has been registered!", listener);
			}
			@Override
			public void onCancel() {}
			@Override
			public void onRegisterFailure() {
				Alerter.failedToRegister(null);
			}
		});
	}
	
	private void showResetPasswordWindow(final LoggedInListener listener){
		resetPasswordPresenter.setEmail(loginPresenter.getEmailField());
		resetPasswordPresenter.show(new IResetPasswordListener(){
			@Override
			public void onCodeSent() {
				Alerter.resetCodeSent(resetPasswordPresenter.getEmail());
				loginPresenter.setEmailField(resetPasswordPresenter.getEmail());
			}
			@Override
			public void onSuccess() {
				loginPresenter.setEmailField(resetPasswordPresenter.getEmail());
				showLoginWindow("Login", listener);
			}
			@Override
			public void onCancel() {
				showLoginWindow(listener);
			}
		});
	}
	
	public interface LoggedInListener{
		public void onLoggedIn();
		public void onCancel();
	}
	
	public String mayStop() {
		return null;
	}

	public void onCancel() {}

	public void onStop() {}
}
