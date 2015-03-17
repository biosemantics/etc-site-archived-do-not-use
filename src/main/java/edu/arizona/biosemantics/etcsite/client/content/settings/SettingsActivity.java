package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.InvalidOTOAccountException;

public class SettingsActivity extends MyAbstractActivity implements ISettingsView.Presenter {

	private IUserServiceAsync userService;
	private ISettingsView view;

	@Inject
	public SettingsActivity(ISettingsView settingsView, 
			PlaceController placeController,
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter, 
			IUserServiceAsync userService) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.view = settingsView;
		this.userService = userService;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		panel.setWidget(view.asWidget());
		
		//there will be a code when user decided to create OTO Account using Google from Google's redirect upon initial request
		String value = Window.Location.getParameter("code");	
		if(value != null) {
			Alerter.startLoading();
			userService.createOTOAccount(Authentication.getInstance().getToken(), value, new AsyncCallback<edu.arizona.biosemantics.oto.common.model.User>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.stopLoading();
					Alerter.failedToCreateOTOAccount(caught);
					populateUserData();
				}
				@Override
				public void onSuccess(edu.arizona.biosemantics.oto.common.model.User result) {
					view.setOTOAccount(result.getUserEmail(), result.getPassword());
					view.setLinkedOTOAccount(result.getUserEmail());
					//populateUserData();
					Alerter.stopLoading();
					Alerter.successfullyCreatedOTOAccount();
				}
			});
		} else {
			populateUserData();
		}
	}
	
	@Override
	public void update() {}

	private void populateUserData() {
		userService.getUser(Authentication.getInstance().getToken(), new AsyncCallback<ShortUser>() {
			@Override
			public void onSuccess(ShortUser user) {
				view.setData(user);
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGetUser(caught);
			}
		});

		
	}

	@Override
	public void onSave() {
		ShortUser user = view.getData();
		userService.update(Authentication.getInstance().getToken(), user, new AsyncCallback<ShortUser>() {
			@Override
			public void onSuccess(ShortUser result) {
				Alerter.savedSettingsSuccesfully();
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToUpdateUser(caught);
			}
		});
		
		/*final String firstName = settingsView.getFirstName();
		final String lastName = settingsView.getLastName();
		final String email = settingsView.getEmail();
		final String affiliation = settingsView.getAffiliation();
		final String bioportalUserId = settingsView.getBioportalUserId();
		final String bioportalAPIKey = settingsView.getBioportalAPIKey();
		
		final String oldPassword;
		
		final boolean matrixGenerationEmail = settingsView.isMatrixGenerationEmailChecked();
		final boolean textCaptureEmail = settingsView.isTextCaptureEmailChecked();
		final boolean treeGenerationEmail = settingsView.isTreeGenerationEmailChecked();
		final boolean taxonomyComparisonEmail = settingsView.isTaxonomyComparisonEmailChecked() ;
		
		
		if (openIdProvider.equals("none"))
			oldPassword = settingsView.getOldPassword();
		else
			oldPassword = firstName+lastName;
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
		}
		if (openIdProvider.equals("none")){ //only require password if this is a local user. 
			if (!newPassword.equals(confirmNewPassword)){
				settingsView.setErrorMessage("Passwords do not match.");
				return;
			} else if (newPassword.length() > 0 && newPassword.length() < 6){
				settingsView.setErrorMessage("Password must be at least 6 characters.");
				return;
			} else if (oldPassword.length() == 0){
				settingsView.setErrorMessage("You must enter your password in order to submit changes.");
				return;
			}
		}
		
		final String password= newPassword.length() > 0 ? newPassword : oldPassword;
		ShortUser newUser = new ShortUser(Authentication.getInstance().getToken().getUserId(), email, firstName, lastName, affiliation, 
				openIdProvider, "", bioportalUserId, bioportalAPIKey,matrixGenerationEmail,textCaptureEmail,treeGenerationEmail,taxonomyComparisonEmail);
		
		userService.update(Authentication.getInstance().getToken(), oldPassword, 
				password, newUser, new AsyncCallback<ShortUser>() {
			@Override
			public void onSuccess(ShortUser result) {
				settingsView.clearPasswords();					
				Alerter.savedSettingsSuccesfully();
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToUpdateUser(caught);
			}
		});*/
	}

	@Override
	public void onPasswordSave() {
		userService.update(Authentication.getInstance().getToken(), view.getCurrentPassword(), view.getNewPassword(), new AsyncCallback<ShortUser>() {
			@Override
			public void onSuccess(ShortUser result) {
				Alerter.savedSettingsSuccesfully();
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToUpdateUser(caught);
			}
		});
	}

	@Override
	public void onNewOTOGoogleAccount() {
		String url = "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email"
				+ "&client_id=" + ServerSetup.getInstance().getSetup().getGoogleClientId() 
				+ "&response_type=code"
				+ "&redirect_uri=" + ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":"; 
		Location.replace(url);
	}

	@Override
	public void onNewOTOAccount(final String email, final String password) {
		Alerter.startLoading();
		userService.createOTOAccount(Authentication.getInstance().getToken(), email, password, new AsyncCallback<edu.arizona.biosemantics.oto.common.model.User>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading();
				Alerter.failedToCreateOTOAccount(caught);
			}
			@Override
			public void onSuccess(edu.arizona.biosemantics.oto.common.model.User result) {
				view.setOTOAccount(result.getUserEmail(), result.getPassword());
				view.setLinkedOTOAccount(email);
				//populateUserData();
				Alerter.stopLoading();
				Alerter.successfullyCreatedOTOAccount();
			}
		});
	}

	@Override
	public void onSaveOTOAccount(final boolean share, final String email, final String password) {
		Alerter.startLoading();
		userService.saveOTOAccount(Authentication.getInstance().getToken(), share, email, password, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading();
				if(caught instanceof InvalidOTOAccountException) {
					Alerter.invalidOTOAccount(caught);
				} else {
					Alerter.failedToSaveOTOAccount(caught);
				}
			}
			@Override
			public void onSuccess(Void result) {
				if(share)
					view.setLinkedOTOAccount(email);
				Alerter.stopLoading();
			} 
		});
	}

}
