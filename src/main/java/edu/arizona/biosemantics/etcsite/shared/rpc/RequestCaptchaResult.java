package edu.arizona.biosemantics.etcsite.shared.rpc;

import java.io.Serializable;

public class RequestCaptchaResult implements Serializable {

	private static final long serialVersionUID = -4801767409715715417L;
	
	private int captchaId;

	public RequestCaptchaResult(){}
	
	public RequestCaptchaResult(int id) {
		this.captchaId = id;
	}

	public int getId() {
		return captchaId;
	}
	
	public void setId(int id){
		this.captchaId = id;
	}
}
