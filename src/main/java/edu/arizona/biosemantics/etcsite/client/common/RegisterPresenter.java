package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;

import edu.arizona.biosemantics.etcsite.client.common.IRegisterView.IRegisterListener;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent;
import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent.AuthenticationEventType;
import edu.arizona.biosemantics.etcsite.client.layout.IEtcSiteView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;

public class RegisterPresenter implements IRegisterView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private IUserServiceAsync userService;
	private IRegisterView view;
	private Dialog dialog;
	private CaptchaPresenter captchaPresenter;
	private PlaceController placeController;
	private EventBus eventBus;

	@Inject
	public RegisterPresenter(IRegisterView view, IUserServiceAsync userService, 
			IAuthenticationServiceAsync authenticationService, PlaceController placeController, @Named("EtcSite")EventBus eventBus) {
		this.view = view;
		this.userService = userService;
		this.authenticationService = authenticationService;
		this.placeController = placeController;
		this.eventBus = eventBus;
		
		view.setPresenter(this);
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("Register");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.setPredefinedButtons();
		dialog.add(view.asWidget());
		
		captchaPresenter = new CaptchaPresenter(view.getCaptchaPanel(), authenticationService);
	}

	@Override
	public void onCancel() {
		dialog.hide();
	}
	
	@Override
	public void onCreate() {
		final String firstName = view.getFirstName();
		final String lastName = view.getLastName();
		final String email = view.getEmail();
		final String password = view.getPassword();
		int captchaId = view.getCaptchaPanel().getId();
		String captchaSolution = view.getCaptchaPanel().getSolution();
		
		//error checking. 
		if (firstName.length() == 0 || lastName.length() == 0 || email.length() == 0 || password.length() == 0){
			view.setErrorMessage("All fields are required.");
			return;
		}
		if (!RegExp.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").test(email)){
			view.setErrorMessage("Email address must be valid.");
			return;
		}
		if (!password.equals(view.getConfirmPassword())){
			view.setErrorMessage("Passwords do not match.");
			return;
		}
		if (password.length() < 6){
			view.setErrorMessage("Password must be at least 6 characters.");
			return;
		}
		if (captchaSolution.length() == 0){
			view.setErrorMessage("Please enter the security code.");
			return;
		}
		
		view.setErrorMessage("");
		authenticationService.signupUser(captchaId, captchaSolution, firstName, lastName, email, password, 
				new AsyncCallback<AuthenticationResult>() {
			@Override
			public void onSuccess(AuthenticationResult result) {
				Authentication auth = Authentication.getInstance();
				auth.setUserId(result.getUser().getId());
				auth.setSessionID(result.getSessionID());
				auth.setEmail(result.getUser().getEmail());
				auth.setFirstName(result.getUser().getFirstName());
				auth.setLastName(result.getUser().getLastName());
				auth.setAffiliation(result.getUser().getAffiliation());
				dialog.hide();
				eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.LOGGEDIN));
				//Alerter.firstLoginCheckAccountInfo();
				placeController.goTo(new HomePlace());
			}
			@Override
			public void onFailure(Throwable caught) {
				captchaPresenter.requestNewCaptcha();
				Alerter.failedToRegister(caught);
			}
		});
	}
	
	@Override
	public void show() {
		view.giveFocus();
		view.clearPasswordBoxes();
		view.setErrorMessage("");
		captchaPresenter.requestNewCaptcha();
		dialog.show();
	}
	
	@Override
	public String getEmail(){
		return view.getEmail();
	}
	
	@Override
	public void clearAllBoxes(){
		view.clearAllBoxes();
	}
}

