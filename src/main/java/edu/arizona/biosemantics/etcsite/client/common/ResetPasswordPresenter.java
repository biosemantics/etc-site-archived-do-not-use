package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView.IResetPasswordListener;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;

public class ResetPasswordPresenter implements IResetPasswordView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private IResetPasswordView resetPasswordView;
	private Dialog dialog;
	private IResetPasswordListener currentListener;
	private CaptchaPresenter captchaPresenter;

	@Inject
	public ResetPasswordPresenter(IResetPasswordView view, IAuthenticationServiceAsync authenticationService) {
		this.resetPasswordView = view;
		resetPasswordView.setPresenter(this);
		this.authenticationService = authenticationService;
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("Forgot Password");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.setPredefinedButtons(PredefinedButton.CANCEL);
		dialog.add(resetPasswordView.asWidget());
		dialog.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				resetPasswordView.clearFields();
				if(currentListener != null)
					currentListener.onCancel();
			}
		});
		
		captchaPresenter = new CaptchaPresenter(view.getCaptchaPanel(), authenticationService);
	}
	
	@Override
	public void show(IResetPasswordListener listener) {
		this.currentListener = listener;
		captchaPresenter.requestNewCaptcha();
		dialog.show();
	}

	@Override
	public void onRequestCode() {
		String openIdProviderId = resetPasswordView.getEmail();
		int captchaId = resetPasswordView.getCaptchaPanel().getId();
		String captchaSolution = resetPasswordView.getCaptchaPanel().getSolution();
		
		//error checking. 
		resetPasswordView.setErrorLabel1("");
		if (openIdProviderId.length() == 0){
			resetPasswordView.setErrorLabel1("Enter your email id.");
			return;
		}
		if (captchaSolution.length() == 0){
			resetPasswordView.setErrorLabel1("Enter the security code.");
			return;
		}
		
		captchaPresenter.requestNewCaptcha();
		
		authenticationService.requestPasswordResetCode(captchaId, captchaSolution, openIdProviderId, new AsyncCallback<Void>(){
			@Override
			public void onSuccess(Void result) {			 
				if (currentListener != null)
					currentListener.onCodeSent();
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToRequestPasswordResetCode(caught);
			}
		});
	}
	
	@Override
	public void onRequestReset() {
		
		String openIdProviderId = resetPasswordView.getEmail();
		String code = resetPasswordView.getCode();
		String newPassword = resetPasswordView.getNewPassword();
		String confirmNewPassword = resetPasswordView.getConfirmNewPassword();
		
		//error checking. 
		resetPasswordView.setErrorLabel2("");
		if (openIdProviderId.length() == 0 || code.length() == 0 || newPassword.length() == 0){
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
		
		authenticationService.resetPassword(openIdProviderId, code, newPassword, new AsyncCallback<Void>(){
			@Override
			public void onSuccess(Void result) {
				resetPasswordView.clearFields();
				dialog.hide();
				if (currentListener != null)
					currentListener.onSuccess();
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToResetPassword(caught);
			}
		});
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

