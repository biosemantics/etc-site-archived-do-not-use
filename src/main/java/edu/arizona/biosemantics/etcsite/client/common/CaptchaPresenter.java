package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.CaptchaPanel;

import edu.arizona.biosemantics.etcsite.shared.model.RequestCaptchaResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class CaptchaPresenter {
	private CaptchaPanel captchaPanel;
	private IAuthenticationServiceAsync authenticationService;
	
	public CaptchaPresenter(CaptchaPanel panel, IAuthenticationServiceAsync authenticationService){
		this.captchaPanel = panel;
		this.authenticationService = authenticationService;
		captchaPanel.setPresenter(this);
	}

	public void requestNewCaptcha() {
		authenticationService.requestCaptcha(new RPCCallback<RequestCaptchaResult>(){
			public void onResult(RequestCaptchaResult result){
				captchaPanel.updateCaptcha(result.getId());
			}
		});
	}
}
