package edu.arizona.biosemantics.etcsite.client.top;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
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
	ImageLabel home;
	
	@UiField
	ImageLabel news;
	
	@UiField
	ImageLabel about;
	
	@UiField
	ImageLabel help;
	
	
	@UiHandler("home")
	void onHomeClick(ClickEvent e) {
		presenter.onHome();
	}
	
	@UiHandler("about")
	void onAboutClick(ClickEvent e) {
		presenter.onAbout();
	}
	
	@UiHandler("news")
	void onNewsClick(ClickEvent e) {
		presenter.onNews();
	}
	
	@UiHandler("help")
	void onHelpClick(ClickEvent e) {
		presenter.onHelp();
	}
	
	@UiHandler("loginLabel")
	void onLoginClick(ClickEvent e) {
		presenter.onLogin();
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
