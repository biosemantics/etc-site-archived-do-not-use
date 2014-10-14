package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptchaPanel;

import edu.arizona.biosemantics.etcsite.shared.model.Captcha;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;

public class CaptchaPresenter {
	
	private CaptchaPanel captchaPanel;
	private IAuthenticationServiceAsync authenticationService;
	
	public CaptchaPresenter(CaptchaPanel panel, IAuthenticationServiceAsync authenticationService){
		this.captchaPanel = panel;
		this.authenticationService = authenticationService;
		captchaPanel.setPresenter(this);
	}

	public void requestNewCaptcha() {
		authenticationService.createCaptcha(new AsyncCallback<Captcha>(){
			@Override
			public void onSuccess(Captcha captcha) {
				captchaPanel.updateCaptcha(captcha.getId());
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToCreateCaptcha(caught);
			}
		});
	}
}
