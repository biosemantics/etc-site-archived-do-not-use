package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.IRegisterView.IRegisterListener;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RegistrationResult;

public class RegisterPresenter implements IRegisterView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private IRegisterView view;
	private PopupPanel dialogBox;
	private IRegisterListener currentListener;
	private CaptchaPresenter captchaPresenter;

	@Inject
	public RegisterPresenter(IRegisterView view, IAuthenticationServiceAsync authenticationService) {
		this.view = view;
		this.authenticationService = authenticationService;
		
		view.setPresenter(this);
		
		dialogBox = new PopupPanel(true){
			@Override
			public void hide(boolean autoClosed){
				if (autoClosed)
					if (currentListener != null)
						currentListener.onCancel();
				super.hide(autoClosed);
			}
		}; //true means that the popup will close when the user clicks outside of it.
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
		
		captchaPresenter = new CaptchaPresenter(view.getCaptchaPanel(), authenticationService);
	}

	@Override
	public void onCancel() {
		dialogBox.hide();
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
		authenticationService.registerAccount(captchaId, captchaSolution, firstName, lastName, email, password, new RPCCallback<RegistrationResult>() {
			@Override
			public void onResult(RegistrationResult result) {
				captchaPresenter.requestNewCaptcha();
				if (result.getResult() == true){
					dialogBox.hide();
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
		dialogBox.center();
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
