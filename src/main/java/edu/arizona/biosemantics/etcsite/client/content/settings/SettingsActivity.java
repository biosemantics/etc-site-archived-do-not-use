package edu.arizona.biosemantics.etcsite.client.content.settings;

import java.util.Map;

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
import edu.arizona.biosemantics.etcsite.client.layout.IEtcSiteView;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.InvalidOTOAccountException;

public class SettingsActivity extends MyAbstractActivity implements ISettingsView.Presenter {

	private IUserServiceAsync userService;
	private ISettingsView view;
	private IEtcSiteView.Presenter etcSitePresenter;

	@Inject
	public SettingsActivity(IEtcSiteView.Presenter etcSitePresenter,
			ISettingsView settingsView, 
			PlaceController placeController,
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter, 
			IUserServiceAsync userService) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.etcSitePresenter = etcSitePresenter;
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
						final MessageBox box = Alerter.startLoading();
						userService.saveOTOAccount(Authentication.getInstance().getToken(), accessToken, new AsyncCallback<edu.arizona.biosemantics.oto.model.User>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading(box);
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
							public void onSuccess(edu.arizona.biosemantics.oto.model.User result) {
								view.setLinkedOTOAccount(result.getUserEmail());
								Alerter.stopLoading(box);
								Window.Location.replace(ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":");
							} 
						});
						break;
					case "settings_create_oto":
						final MessageBox boxCreate = Alerter.startLoading();
						userService.createOTOAccount(Authentication.getInstance().getToken(), accessToken, new AsyncCallback<edu.arizona.biosemantics.oto.model.User>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading(boxCreate);
								MessageBox box = Alerter.failedToCreateOTOAccount(caught);
								box.addHideHandler(new HideHandler() {
									@Override
									public void onHide(HideEvent event) {
										Window.Location.replace(ServerSetup.getInstance().getSetup().getGoogleRedirectURI() + "#" + SettingsPlace.class.getSimpleName() + ":");
									}
								});
							}
							@Override
							public void onSuccess(edu.arizona.biosemantics.oto.model.User result) {
								view.setOTOAccount(result.getUserEmail(), result.getPassword());
								view.setLinkedOTOAccount(result.getUserEmail());
								//populateUserData();
								Alerter.stopLoading(boxCreate);
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
		String firstName=view.getFirstName();
		String lastName=view.getLastName();
		String affiliation=view.getAffiliation();
		
		if(firstName.trim().isEmpty()) {
			Alerter.firstNameRequired();
			return;
		}
		
		if(lastName.trim().isEmpty()) {
			Alerter.lastNameRequired();
			return;
		}
		
		userService.updateName(Authentication.getInstance().getToken(), firstName,lastName,affiliation, new AsyncCallback<ShortUser>() {
				@Override
				public void onSuccess(ShortUser result) {
					Alerter.savedSettingsSuccesfully();
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToUpdateUser(caught);
				}
		});

		etcSitePresenter.getView().setName(firstName);
		
	}
	


	@Override
	public void onPasswordSave() {
		if(!view.getNewPassword().equals(view.getConfirmPassword())) {
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
				Alerter.failedToUpdatePassword(caught);
			}
		});
	}
	
	@Override
	public void onSaveBioportal() {                 
		String bioportalApiKey = view.getBioportalApiKey();
		String bioportalUserId = view.getBioportalUserId();
		
		if(bioportalApiKey.isEmpty()) {
			Alerter.bioportalApiKeyRequired();
			return;
		}
		
		if(bioportalUserId.isEmpty()) {
			Alerter.bioportalUserIdRequired();
			return;
		}
		
		userService.updateBioportal(Authentication.getInstance().getToken(), bioportalApiKey, bioportalUserId, new AsyncCallback<ShortUser>() {
				@Override
				public void onSuccess(ShortUser result) {
					Alerter.savedSettingsSuccesfully();
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToUpdateBioPortalInfo(caught);
				}
		});
		
	}
	
	@Override
	public void onEmailNotification() {                 
		Map<String, Boolean>  emailPreference = view.getEmailPreference();
		
		userService.updateEmailNotification(Authentication.getInstance().getToken(), emailPreference, new AsyncCallback<ShortUser>() {
				@Override
				public void onSuccess(ShortUser result) {
					Alerter.savedSettingsSuccesfully();
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToUpdateEmailNotification(caught);
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
			final MessageBox box = Alerter.startLoading();
			userService.createOTOAccount(Authentication.getInstance().getToken(), email, password, new AsyncCallback<edu.arizona.biosemantics.oto.model.User>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.stopLoading(box);
					Alerter.failedToCreateOTOAccount(caught);
				}
				@Override
				public void onSuccess(edu.arizona.biosemantics.oto.model.User result) {
					view.setOTOAccount(result.getUserEmail(), result.getPassword());
					view.setLinkedOTOAccount(email);
					userService.saveOTOAccount(Authentication.getInstance().getToken(), true, email, password, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.stopLoading(box);
							Alerter.failedToCreateOTOAccount(caught);
						}
						@Override
						public void onSuccess(Void result) {
							//populateUserData();
							Alerter.stopLoading(box);
							Alerter.successfullyCreatedOTOAccount();
						}
					});
				}
			});
		}
	}

	@Override
	public void onSaveOTOAccount(final boolean share, final String email, final String password) {
		if(!view.isLinkedOTOAccount()) {
			final MessageBox box = Alerter.startLoading();
			userService.saveOTOAccount(Authentication.getInstance().getToken(), share, email, password, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.stopLoading(box);
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
					Alerter.stopLoading(box);
					Alerter.savedSuccessfully();
				} 
			});
		} else {
			Alerter.savedSuccessfully();
		}
	}

	@Override
	public void onLinkAccount(final Boolean share, final String email, final String password) {
		final MessageBox box = Alerter.startLoading();
		userService.saveOTOAccount(Authentication.getInstance().getToken(), share, email, password, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading(box);
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
				Alerter.stopLoading(box);
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
