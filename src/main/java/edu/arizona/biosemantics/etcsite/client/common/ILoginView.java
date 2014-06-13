package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.IsWidget;

public interface ILoginView extends IsWidget {

	public interface Presenter {

		void onLogin();

		void onCancel();

		void show(ILoginListener listener);
		
		void setEmailField(String str);
		
		void setMessage(String str);

		void onRegisterRequest();

		void onResetPasswordRequest();
		
		void onSignInWithGoogle();

		String getEmailField();
	}
	
	public interface ILoginListener {
		void onLogin();
		void onLoginFailure();
		void onRegisterRequest();
		void onCancel();
		void onResetPasswordRequest();
	}
	
	void setPresenter(Presenter presenter);
	
	void clearPasswordTextBox();
	
	void giveLoginFocus();
	
	String getUsername();

	String getPassword();
	
	void setEmail(String str);
	
	void setMessage(String str);
}
