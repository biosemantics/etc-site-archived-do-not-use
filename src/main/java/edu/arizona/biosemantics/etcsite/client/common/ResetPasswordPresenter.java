package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView.IResetPasswordListener;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.PasswordResetResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class ResetPasswordPresenter implements IResetPasswordView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private IResetPasswordView resetPasswordView;
	private PopupPanel dialogBox;
	private IResetPasswordListener currentListener;
	private CaptchaPresenter captchaPresenter;

	@Inject
	public ResetPasswordPresenter(IResetPasswordView view, IAuthenticationServiceAsync authenticationService) {
		this.resetPasswordView = view;
		resetPasswordView.setPresenter(this);
		this.authenticationService = authenticationService;
		dialogBox = new PopupPanel(true){
			@Override
			public void hide(boolean autoClosed){
				if (autoClosed)
					if (currentListener != null)
						onCancel();
				super.hide(autoClosed);
			}
		}; //true means that the popup will close when the user clicks outside of it.
		dialogBox.setGlassEnabled(true);
		dialogBox.add(resetPasswordView.asWidget());
		
		captchaPresenter = new CaptchaPresenter(view.getCaptchaPanel(), authenticationService);
	}
	
	@Override
	public void show(IResetPasswordListener listener) {
		this.currentListener = listener;
		captchaPresenter.requestNewCaptcha();
		dialogBox.center();
	}

	@Override
	public void onRequestCode() {
		String nonUniqueId = resetPasswordView.getEmail();
		int captchaId = resetPasswordView.getCaptchaPanel().getId();
		String captchaSolution = resetPasswordView.getCaptchaPanel().getSolution();
		
		//error checking. 
		resetPasswordView.setErrorLabel1("");
		if (nonUniqueId.length() == 0){
			resetPasswordView.setErrorLabel1("Enter your email id.");
			return;
		}
		if (captchaSolution.length() == 0){
			resetPasswordView.setErrorLabel1("Enter the security code.");
			return;
		}
		
		captchaPresenter.requestNewCaptcha();
		
		authenticationService.requestPasswordResetCode(captchaId, captchaSolution, nonUniqueId, new RPCCallback<PasswordResetResult>(){
			@Override
			public void onResult(PasswordResetResult result) {
				if (result.getResult()){ //code generation was successful. 
					if (currentListener != null)
						currentListener.onCodeSent(result.getMessage());
				} else {
					if (currentListener != null)
						currentListener.onFailure(result.getMessage());
				}
			}
		});
	}
	
	@Override
	public void onRequestReset() {
		
		String nonUniqueId = resetPasswordView.getEmail();
		String code = resetPasswordView.getCode();
		String newPassword = resetPasswordView.getNewPassword();
		String confirmNewPassword = resetPasswordView.getConfirmNewPassword();
		
		//error checking. 
		resetPasswordView.setErrorLabel2("");
		if (nonUniqueId.length() == 0 || code.length() == 0 || newPassword.length() == 0){
			resetPasswordView.setErrorLabel2("All fields are required.");
			return;
		}
		if (!newPassword.equals(confirmNewPassword)){
			resetPasswordView.setErrorLabel2("Passwords do not match.");
			return;
		}
		if (newPassword.length() < 6){
			resetPasswordView.setErrorLabel2("New password must be at least 6 characters.");
			return;
		}
		
		authenticationService.requestPasswordReset(nonUniqueId, code, newPassword, new RPCCallback<PasswordResetResult>(){
			@Override
			public void onResult(PasswordResetResult result) {
				if (result.getResult()){
					resetPasswordView.clearFields();
					dialogBox.hide();
					
					if (currentListener != null)
						currentListener.onSuccess(result.getMessage());
				} else {
					if (currentListener != null)
						currentListener.onFailure(result.getMessage());
				}
			}
		});
	}

	private void onCancel(){
		resetPasswordView.clearFields();
		currentListener.onCancel();
	}
	
	@Override
	public String getEmail() {
		return resetPasswordView.getEmail();
	}
	
	@Override
	public void setEmail(String emailField) {
		resetPasswordView.setEmail(emailField);
	}
}

