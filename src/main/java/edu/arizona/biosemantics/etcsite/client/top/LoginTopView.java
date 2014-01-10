package edu.arizona.biosemantics.etcsite.client.top;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.common.ImageLabel;

public class LoginTopView extends Composite implements ILoginTopView {

	private static LoginTopViewUiBinder uiBinder = GWT.create(LoginTopViewUiBinder.class);

	interface LoginTopViewUiBinder extends UiBinder<Widget, LoginTopView> {
	}

	public LoginTopView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private Presenter presenter;

	@UiField
	Button loginButton;
	
	@UiField
	TextBox userTextBox;
	
	@UiField
	PasswordTextBox passwordTextBox;
	
	@UiField
	ImageLabel help;

	@UiHandler("loginButton")
	void onClick(ClickEvent e) {
		presenter.onLogin();
	}
	
	@UiHandler("help")
	void onHelpClick(ClickEvent e) {
		presenter.onHelp();
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public String getUser() {
		return this.userTextBox.getText();
	}

	@Override
	public String getPassword() {
		return this.passwordTextBox.getText();
	}
}
