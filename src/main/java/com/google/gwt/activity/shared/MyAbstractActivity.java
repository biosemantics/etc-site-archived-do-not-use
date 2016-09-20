package com.google.gwt.activity.shared;

import com.google.gwt.place.shared.PlaceController;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
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

	
	public String mayStop() {
		return null;
	}

	public void onCancel() {}

	public void onStop() {}
}
