package edu.arizona.biosemantics.etcsite.client.layout;

import java.util.Arrays;
import java.util.LinkedList;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.AuthenticationPresenter;
import edu.arizona.biosemantics.etcsite.client.common.AuthenticationToPlaceGoer;
import edu.arizona.biosemantics.etcsite.client.common.RequiresAuthenticationPlace;
import edu.arizona.biosemantics.etcsite.client.common.ResumeTaskToPlaceGoer;
import edu.arizona.biosemantics.etcsite.client.common.ToPlaceGoer;
import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsPlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;

public class EtcSitePresenter implements IEtcSiteView.Presenter {

	private IAuthenticationServiceAsync authenticationService;
	private ITaskServiceAsync taskService;
	private IEtcSiteView view;
	private AuthenticationPresenter authenticationPresenter;
	private AuthenticationToPlaceGoer authenticationToPlaceGoer;
	private ResumeTaskToPlaceGoer resumeTaskToPlaceGoer;
	private PlaceController placeController;
	private ResumableTaskFinder resumableTaskFinder;
	private EventBus tasksBus;
	private HandlerRegistration resumableTasksRegistration;

	@Inject
	public EtcSitePresenter(IEtcSiteView view,
			IAuthenticationServiceAsync authenticationService, 
			ITaskServiceAsync taskService,
			AuthenticationPresenter authenticationPresenter, 
			AuthenticationToPlaceGoer authenticationToPlaceGoer, 
			ResumeTaskToPlaceGoer resumeTaskToPlaceGoer, 
			PlaceController placeController, 
			ResumableTaskFinder resumableTaskFinder, 
			@Named("Tasks") final EventBus tasksBus) {
		this.view = view;
		view.setPresenter(this);
		this.authenticationPresenter = authenticationPresenter;
		this.authenticationService = authenticationService;
		this.taskService = taskService;
		this.authenticationToPlaceGoer = authenticationToPlaceGoer;
		this.resumeTaskToPlaceGoer = resumeTaskToPlaceGoer;
		this.placeController = placeController;
		this.resumableTaskFinder = resumableTaskFinder;
		this.tasksBus = tasksBus;
		updateAuthentication();
	}

	@Override
	public void updateAuthentication() {
		
		Authentication authentication = Authentication.getInstance();
		if(authentication.isSet()) {
			authenticationService.isValidSession(authentication.getToken(), new AsyncCallback<AuthenticationResult>() {
				@Override
				public void onSuccess(AuthenticationResult result) {
					if(result.getResult()) {
						setLoggedIn();
					} else {
						Authentication.getInstance().destroy();
						setLoggedOut();
					}
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToValidateSession(caught);
				}
			});
		} else if (authentication.getExternalAccessToken() != null){ //check to see if this is a redirect from Google
			String accessToken = authentication.getExternalAccessToken();
			authentication.setExternalAccessToken(null); //don't need this anymore. 
			authenticationService.loginOrSignupWithGoogle(accessToken, new AsyncCallback<AuthenticationResult>(){
				@Override
				public void onSuccess(AuthenticationResult result) {
					if(result.getResult()) {
						Authentication auth = Authentication.getInstance();
						auth.setUserId(result.getUser().getId());
						auth.setSessionID(result.getSessionID());
						auth.setEmail(result.getUser().getEmail());
						auth.setFirstName(result.getUser().getFirstName());
						auth.setLastName(result.getUser().getLastName());
						auth.setAffiliation(result.getUser().getAffiliation());
						setLoggedIn();
					} else {
						Alerter.failedToLoginWithgGoogle(null);
						setLoggedOut();
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToLoginWithgGoogle(caught);
				}
			});
		} else {
			setLoggedOut();
		}
	}

	@Override
	public IEtcSiteView getView() {
		return view;
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
	public void onNews() {
		placeController.goTo(new NewsPlace());
	}

	@Override
	public void onTaskManager() {
		this.authenticationToPlaceGoer.goTo(new TaskManagerPlace(), null);
	}

	@Override
	public void onFileManager() {
		this.authenticationToPlaceGoer.goTo(new FileManagerPlace(), null);
	}

	@Override
	public void onAccount() {
		this.authenticationToPlaceGoer.goTo(new SettingsPlace(), null);
	}

	@Override
	public void onTextCapture() {
		this.authenticationToPlaceGoer.goTo(new SemanticMarkupInputPlace(), new LinkedList<ToPlaceGoer>(Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onMatrixGeneration() {
		this.authenticationToPlaceGoer.goTo(new MatrixGenerationInputPlace(), new LinkedList<ToPlaceGoer>(Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onTreeGeneration() {
		this.authenticationToPlaceGoer.goTo(new TreeGenerationInputPlace(), new LinkedList<ToPlaceGoer>(Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onTaxonomyComparison() {
		this.authenticationToPlaceGoer.goTo(new TaxonomyComparisonPlace(), new LinkedList<ToPlaceGoer>(Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onVisualization() {
		placeController.goTo(new VisualizationPlace());
	}

	@Override
	public void onOpenHelpInNewWindow() {
		//String href = Window.Location.createUrlBuilder().setHash("myPlaceToken").buildString(); 
		//Window.open(href, "_blank", "");
		Window.open("help.html", "_blank", "");
	}
	
	@Override
	public void onLoginLogout() {
		if(view.isLogout()) {
			setLoggedOut();
			Authentication.getInstance().destroy();
		} else if(view.isLogin())
			authenticationPresenter.showLoginWindow();
	}

	private void setLoggedIn() {
		resumableTaskFinder.start();
		view.setLogout();
		
		resumableTasksRegistration = tasksBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEvent.ResumableTasksEventHandler() {
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				view.setResumableTasks(!resumableTasksEvent.getTasks().isEmpty());
			}
		});
	}
	
	private void setLoggedOut() {
		resumableTaskFinder.stop();
		if(resumableTasksRegistration != null)
			resumableTasksRegistration.removeHandler();
		
		view.setLogin();
		if(placeController.getWhere() instanceof RequiresAuthenticationPlace) {
			placeController.goTo(new HomePlace());
		}
	}
}
