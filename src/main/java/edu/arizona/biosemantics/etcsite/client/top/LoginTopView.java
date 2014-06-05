package edu.arizona.biosemantics.etcsite.client.top;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
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
	ImageLabel help;

	@UiField
	Label loginLabel;
	
	@UiField
	Label registerLabel;
	
	
	@UiHandler("help")
	void onHelpClick(ClickEvent e) {
		presenter.onHelp();
	}
	
	@UiHandler("loginLabel")
	void onLoginClick(ClickEvent e) {
		presenter.onLogin();
	}
	
	@UiHandler("registerLabel")
	void onRegisterClick(ClickEvent e) {
		presenter.onRegister();
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
