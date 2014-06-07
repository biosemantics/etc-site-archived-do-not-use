package edu.arizona.biosemantics.etcsite.client.top;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IMessageOkView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.content.help.HelpPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsPlace;
import edu.arizona.biosemantics.etcsite.client.top.ILoginTopView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class LoggedOutActivity extends MyAbstractActivity implements Presenter {
	
	private ILoginTopView loginTopView;

	@Inject
	public LoggedOutActivity(ILoginTopView loginTopView, 
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter, 
			IMessageOkView.Presenter messagePresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter, messagePresenter);
		this.loginTopView = loginTopView;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		Authentication authentication = Authentication.getInstance();
		if(authentication.isSet()) {
			authenticationService.isValidSession(authentication.getToken(), new RPCCallback<AuthenticationResult>() {
				@Override
				public void onResult(AuthenticationResult result) {
					if(result.getResult()) {
						placeController.goTo(new LoggedInPlace());
					} else {
						Authentication.getInstance().destroy(); //if this session is not valid, clear the authentication cookies.
						setLoginView(panel);
					}
				}
			});
		} else {
			setLoginView(panel);
		}
	}

	private void setLoginView(AcceptsOneWidget panel) {
		loginTopView.setPresenter(this);
		panel.setWidget(loginTopView.asWidget());
	}

	@Override
	public void onHome() {
		placeController.goTo(new HomePlace());
	}
	
	@Override
	public void onAbout() {
		placeController.goTo(new AboutPlace());
	}
	
	@Override
	public void onHelp() {
		placeController.goTo(new HelpPlace());
	}
	
	@Override
	public void onNews() {
		placeController.goTo(new NewsPlace());
	}

	@Override
	public void onLogin() {
		if(Authentication.getInstance().isSet()) {
			placeController.goTo(new LoggedInPlace());
		} else {
			showLoginWindow();
		}
	}
	
	@Override
	public void onRegister() {
		if(Authentication.getInstance().isSet()) {
			placeController.goTo(new LoggedInPlace());
		} else {
			showRegisterWindow();
		}
	}

	@Override
	public void update() {}

	
	
	/*private void doGotoPlace(final HasTaskPlace gotoPlace, IHasTasksServiceAsync tasksService) {
		tasksService.getLatestResumable(Authentication.getInstance().getToken(),
				new RPCCallback<Task>() {
			@Override
			public void onResult(final Task task) {
				if(task != null) 
					messageConfirmPresenter.show( _ags
						"Resumable Task", "You have a resumable task of this type", "Start new", "Resume", new IConfirmListener() {
							public void onConfirm() {
								gotoPlace.setTask(task);
								placeController.goTo(gotoPlace);
							}
							public void onCancel() {
								placeController.goTo(gotoPlace);
							}
						});
				else 
					placeController.goTo(gotoPlace);
			}
		});
	}*/
}
