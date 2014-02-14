package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginView extends Composite implements ILoginView {

	private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

	interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
	}

	@UiField
	TextBox usernameTextBox;
	
	@UiField
	TextBox passwordTextBox;
	
	private Presenter presenter;

	public LoginView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("loginButton")
	public void onLogin(ClickEvent event) {
		presenter.onLogin();
	}
	
	@UiHandler("cancelButton")
	public void onCancel(ClickEvent event) {
		presenter.onCancel();
	}
	
	@Override
	public String getUsername() {
		return usernameTextBox.getText();
	}

	@Override
	public String getPassword() {
		return passwordTextBox.getText();
	}
}
