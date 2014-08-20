package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;

import edu.arizona.biosemantics.etcsite.client.common.IRegisterView.IRegisterListener;
import edu.arizona.biosemantics.etcsite.shared.model.RegistrationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class RegisterPresenter implements IRegisterView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private IRegisterView view;
	private Dialog dialog;
	private IRegisterListener currentListener;
	private CaptchaPresenter captchaPresenter;

	@Inject
	public RegisterPresenter(IRegisterView view, IAuthenticationServiceAsync authenticationService) {
		this.view = view;
		this.authenticationService = authenticationService;
		
		view.setPresenter(this);
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeadingText("Register");
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
		if (currentListener != null)
			currentListener.onCancel();
	}
	
	@Override
	public void onCreate() {
		String firstName = view.getFirstName();
		String lastName = view.getLastName();
		String email = view.getEmail();
		String password = view.getPassword();
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
			view.setErrorMessage("Enter the security code.");
			return;
		}
		
		view.setErrorMessage("");
		authenticationService.registerLocalAccount(captchaId, captchaSolution, firstName, lastName, email, password, new RPCCallback<RegistrationResult>() {
			@Override
			public void onResult(RegistrationResult result) {
				captchaPresenter.requestNewCaptcha();
				if (result.getResult() == true){
					dialog.hide();
					currentListener.onRegister(result.getMessage());
				} else {
					view.getCaptchaPanel().clearText();
					currentListener.onRegisterFailure(result.getMessage());
				}
			}
		});
	}
	
	@Override
	public void show(IRegisterListener listener) {
		this.currentListener = listener;
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

