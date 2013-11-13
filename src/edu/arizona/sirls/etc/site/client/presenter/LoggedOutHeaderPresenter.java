package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.HelpEvent;
import edu.arizona.sirls.etc.site.client.event.HomeEvent;
import edu.arizona.sirls.etc.site.client.event.LoginEvent;
import edu.arizona.sirls.etc.site.client.view.ImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationResult;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;

public class LoggedOutHeaderPresenter implements Presenter {

	private HandlerManager eventBus;
	public interface Display {
		Button getLoginButton();
		TextBox getUserField();
		PasswordTextBox getPasswordField();
		ImageLabelComposite getHelpImageLabelComposite();
		Widget asWidget();
	}
	private final Display display;
	private IAuthenticationServiceAsync authenticationService;

	public LoggedOutHeaderPresenter(HandlerManager eventBus, Display display, 
			IAuthenticationServiceAsync authenticationService) {
		this.eventBus = eventBus;
		this.display = display;
		this.authenticationService = authenticationService;
		bind();
	}

	private void bind() {				
		LoginButtonClickHandler handler = new LoginButtonClickHandler(
				eventBus, authenticationService, display.getUserField(),
				display.getPasswordField(), display.getLoginButton(), null);
		handler.setTarget(new HomeEvent());
		display.getLoginButton().addClickHandler(handler);
		display.getUserField().addKeyUpHandler(handler);
		display.getPasswordField().addKeyUpHandler(handler);
		display.getLoginButton().addKeyUpHandler(handler);
				/*new ClickHandler() {
			public void onClick(ClickEvent event) {
				authenticationService.login(display.getUserField().getText(), 
						display.getPasswordField().getText(), 
						new AsyncCallback<AuthenticationResult>() {
							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}
							@Override
							public void onSuccess(AuthenticationResult result) {
								if(result.getResult()) {
									eventBus.fireEvent(new LoginEvent(result.getUsername(), 
											result.getSessionID()));
									eventBus.fireEvent(new HomeEvent());
								} else 
									System.out.println("Problem");
									//TODO do some popup error message
							}
				});
			}
		});*/
		display.getHelpImageLabelComposite().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new HelpEvent());
			}
		});
	}


	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(display.asWidget());
	}

}
