package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.CaptchaPanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface IResetPasswordView extends IsWidget {

	public interface Presenter {

		void show(IResetPasswordListener listener);

		void onRequestCode();
		
		void onRequestReset();

		String getEmail();
		
		void setEmail(String emailField);
	}
	
	public interface IResetPasswordListener {
		
		void onCodeSent();

		void onCancel();
		
		void onSuccess();
	}
	
	void setPresenter(Presenter presenter);

	void clearFields();
	
	void setErrorLabel1(String str);
	
	void setErrorLabel2(String str);
	
	String getEmail();
	
	void setEmail(String emailField);
	
	String getCode();

	String getNewPassword();
	
	String getConfirmNewPassword();
	
	CaptchaPanel getCaptchaPanel();
	
}

