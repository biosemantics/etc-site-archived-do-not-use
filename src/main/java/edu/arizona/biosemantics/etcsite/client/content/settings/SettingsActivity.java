package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IMessageOkView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.shared.db.User;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.UpdateUserResult;

public class SettingsActivity extends MyAbstractActivity implements ISettingsView.Presenter {

	private ISettingsView settingsView;

	@Inject
	public SettingsActivity(ISettingsView settingsView, 
			PlaceController placeController,
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter, 
			IMessageOkView.Presenter messagePresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter, messagePresenter);
		this.settingsView = settingsView;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		settingsView.setPresenter(this);
		panel.setWidget(settingsView.asWidget());
		
		authenticationService.getUser(Authentication.getInstance().getToken(), new RPCCallback<User>() {
			@Override
			public void onResult(User user) {
				settingsView.setData(user);
			}
		});
	}

	@Override
	public void onSubmit() {
		final String firstName = settingsView.getFirstName();
		final String lastName = settingsView.getLastName();
		final String email = settingsView.getEmail();
		final String affiliation = settingsView.getAffiliation();
		final String bioportalUserId = settingsView.getBioportalUserId();
		final String bioportalAPIKey = settingsView.getBioportalAPIKey();
		final String oldPassword = settingsView.getOldPassword();
		final String newPassword = settingsView.getNewPassword();
		final String confirmNewPassword = settingsView.getConfirmNewPassword();
		
		//error checking. 
		settingsView.setErrorMessage("");
		if (firstName.length() == 0){
			settingsView.setErrorMessage("First Name is a required field."); 
			return;
		} else if (lastName.length() == 0){
			settingsView.setErrorMessage("Last Name is a required field."); 
			return;
		} else if (!RegExp.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").test(email)){
			settingsView.setErrorMessage("Email address must be valid.");
			return;
		} else if (!newPassword.equals(confirmNewPassword)){
			settingsView.setErrorMessage("Passwords do not match.");
			return;
		} else if (newPassword.length() > 0 && newPassword.length() < 6){
			settingsView.setErrorMessage("Password must be at least 6 characters.");
			return;
		} else if (oldPassword.length() == 0){
			settingsView.setErrorMessage("You must enter your password in order to submit changes.");
			return;
		}
		
		//if the user has specified a new password, send it. If not, simply send the old password
		//	- it will be encrypted and stored either way. 
		final String password = newPassword.length() > 0 ? newPassword : oldPassword;
			
		authenticationService.updateUser(Authentication.getInstance().getToken(), oldPassword, firstName, lastName, email, password, affiliation, bioportalUserId, bioportalAPIKey, new RPCCallback<UpdateUserResult>() {
			@Override
			public void onResult(UpdateUserResult result) {
				if (result.getResult()){
					//save the new session id that matches the new password. 
					Authentication auth = Authentication.getInstance();
					auth.setSessionID(result.getNewSessionId());
					
					settingsView.clearPasswords();						
				}
				messagePresenter.setMessage(result.getMessage());
				messagePresenter.show();
			}
		});
	}

	@Override
	public void update() {}

}
