package edu.arizona.biosemantics.etcsite.client.content.settings;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

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
		Place place = placeController.getWhere();
		if(place instanceof SettingsPlace) {
			SettingsPlace settingsPlace = ((SettingsPlace)place);
			
			//there will be a code when user decided to create OTO Account using Google from Google's redirect upon initial request
			String action = settingsPlace.getAction();
			String accessToken = settingsPlace.getAccessToken();
			if(accessToken != null && accessToken != null) {
				switch(action) {
					case "settings_save_oto":
						Alerter.startLoading();
						userService.saveOTOAccount(Authentication.getInstance().getToken(), accessToken, new AsyncCallback<edu.arizona.biosemantics.oto.common.model.User>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading();
								MessageBox box;
								if(caught instanceof InvalidOTOAccountException) {
									box = Alerter.invalidOTOAccount(caught);
								} else {
									box = Alerter.failedToSaveOTOAccount(caught);
								}
								box.addHideHandler(new HideHandler() {
									@Override
									public void onHide(HideEvent event) {
										Window.Location.replace(ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":");
									}
								});
							}
							@Override
							public void onSuccess(edu.arizona.biosemantics.oto.common.model.User result) {
								view.setLinkedOTOAccount(result.getUserEmail());
								Alerter.stopLoading();
								Window.Location.replace(ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":");
							} 
						});
						break;
					case "settings_create_oto":
						Alerter.startLoading();
						userService.createOTOAccount(Authentication.getInstance().getToken(), accessToken, new AsyncCallback<edu.arizona.biosemantics.oto.common.model.User>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading();
								MessageBox box = Alerter.failedToCreateOTOAccount(caught);
								box.addHideHandler(new HideHandler() {
									@Override
									public void onHide(HideEvent event) {
										Window.Location.replace(ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":");
									}
								});
							}
							@Override
							public void onSuccess(edu.arizona.biosemantics.oto.common.model.User result) {
								view.setOTOAccount(result.getUserEmail(), result.getPassword());
								view.setLinkedOTOAccount(result.getUserEmail());
								//populateUserData();
								Alerter.stopLoading();
								MessageBox box = Alerter.successfullyCreatedOTOAccount();
								box.addHideHandler(new HideHandler() {
									@Override
									public void onHide(HideEvent event) {
										Window.Location.replace(ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":");
									}
								});
							}
						});
						break;
					default:
						populateUserData();
				}
			}
		}
		
		populateUserData();
		view.setPresenter(this);
		panel.setWidget(view.asWidget());
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
		
		if(user.getFirstName().trim().isEmpty()) {
			Alerter.firstNameRequired();
			return;
		}
		
		if(user.getLastName().trim().isEmpty()) {
			Alerter.lastNameRequired();
			return;
		}
		
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
	}

	@Override
	public void onPasswordSave() {
		if(view.getNewPassword().equals(view.getConfirmPassword())) {
			Alerter.passwordsDontMatch();
			return;
		}
		if (view.getNewPassword().length() < 6){
			Alerter.passwordLengthNotMet();
			return;
		} 
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
		String url = "https://accounts.google.com/o/oauth2/auth"
				+ "?scope=https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email"
				+ "&client_id=" + ServerSetup.getInstance().getSetup().getGoogleClientId()
				+ "&redirect_uri=" + ServerSetup.getInstance().getSetup().getGoogleRedirectURI() 
				+ "&state=" + URL.encodeQueryString(SettingsPlace.class.getSimpleName() + "&action=settings_create_oto")
				+ "&response_type=token";
		
		/*String url = "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email"
				+ "&client_id=" + ServerSetup.getInstance().getSetup().getGoogleClientId() 
				+ "&response_type=code"
				+ "&redirect_uri=" + ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":"; */
		Location.replace(url);
	}

	@Override
	public void onNewOTOAccount(final String email, final String password, final String passwordConfirm) {
		if(!password.equals(passwordConfirm)) {
			Alerter.passwordsDontMatch();
		} else {
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
				Alerter.savedSuccessfully();
			} 
		});
	}

	@Override
	public void onLinkAccount(final Boolean share, final String email, final String password) {
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

	@Override
	public void onExistingOTOGoogleAccount() {
		String url = "https://accounts.google.com/o/oauth2/auth"
				+ "?scope=https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email"
				+ "&client_id=" + ServerSetup.getInstance().getSetup().getGoogleClientId()
				+ "&redirect_uri=" + ServerSetup.getInstance().getSetup().getGoogleRedirectURI() 
				+ "&state=" + URL.encodeQueryString(SettingsPlace.class.getSimpleName() + "&action=settings_save_oto")
				+ "&response_type=token";
		
		Location.replace(url);
		
		/*String url = "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/userinfo.profile%20https://www.googleapis.com/auth/userinfo.email"
				+ "&client_id=" + ServerSetup.getInstance().getSetup().getGoogleClientId() 
				+ "&state=link"
				+ "&response_type=code"
				+ "&redirect_uri=" + ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":";
		Location.replace(url);*/
	}

}
