package edu.arizona.biosemantics.etcsite.client.layout;

import java.util.Arrays;
import java.util.LinkedList;

import com.google.gwt.http.client.URL;
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
import edu.arizona.biosemantics.etcsite.client.content.gettingstarted.GettingStartedPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.MatrixGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsPlace;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.OntologizeInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.sample.SamplePlace;
import edu.arizona.biosemantics.etcsite.client.content.semanticMarkup.SemanticMarkupInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison.TaxonomyComparisonInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.TreeGenerationInputPlace;
import edu.arizona.biosemantics.etcsite.client.content.visualization.VisualizationPlace;
import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent;
import edu.arizona.biosemantics.etcsite.client.event.AuthenticationEvent.AuthenticationEventType;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.LoginGoogleResult;
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
	private EventBus eventBus;
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
			@Named("EtcSite") final EventBus eventBus) {
		this.view = view;
		view.setPresenter(this);
		this.authenticationPresenter = authenticationPresenter;
		this.authenticationService = authenticationService;
		this.taskService = taskService;
		this.authenticationToPlaceGoer = authenticationToPlaceGoer;
		this.resumeTaskToPlaceGoer = resumeTaskToPlaceGoer;
		this.placeController = placeController;
		this.resumableTaskFinder = resumableTaskFinder;
		this.eventBus = eventBus;
		updateAuthentication();
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(AuthenticationEvent.TYPE, new AuthenticationEvent.AuthenticationEventHandler() {
			@Override
			public void onAuthentication(AuthenticationEvent event) {
				switch(event.getType()) {
				case LOGGEDIN:
					setLoggedIn();
					break;
				case LOGGEDOUT:
					setLoggedOut();
					break;
				case TO_BE_DETERMINED:
					updateAuthentication();
					break;
				default:
					break;				
				}
			}
		});
	}

	@Override
	public void updateAuthentication() {
		
		Authentication authentication = Authentication.getInstance();
		if(authentication.isSet()) {
			authenticationService.isValidSession(authentication.getToken(), new AsyncCallback<AuthenticationResult>() {
				@Override
				public void onSuccess(AuthenticationResult result) {
					if(result.getResult()) {
						eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.LOGGEDIN));
					} else {
						Authentication.getInstance().destroy();
						eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.LOGGEDOUT));
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
			authenticationService.loginOrSignupWithGoogle(accessToken, new AsyncCallback<LoginGoogleResult>(){
				@Override
				public void onSuccess(LoginGoogleResult loginGoogleResult) {
					if(loginGoogleResult.getAuthenticationResult().getResult()) {
						AuthenticationResult result = loginGoogleResult.getAuthenticationResult();
						Authentication auth = Authentication.getInstance();
						auth.setUserId(result.getUser().getId());
						auth.setSessionID(result.getSessionID());
						auth.setEmail(result.getUser().getEmail());
						auth.setFirstName(result.getUser().getFirstName());
						auth.setLastName(result.getUser().getLastName());
						auth.setAffiliation(result.getUser().getAffiliation());
						eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.LOGGEDIN));
						
						if(loginGoogleResult.isNewlyRegistered()) {
							//Alerter.firstLoginCheckAccountInfo();
							placeController.goTo(new HomePlace());
						}
					} else {
						Alerter.failedToLoginWithgGoogle(null);
						eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.LOGGEDOUT));
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToLoginWithgGoogle(caught);
				}
			});
		} else {
			eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.LOGGEDOUT));
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
	public void onGetStarted() {
		placeController.goTo(new GettingStartedPlace());
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
		this.authenticationToPlaceGoer.goTo(new SemanticMarkupInputPlace(), new LinkedList<ToPlaceGoer>());//Arrays.asList(this.resumeTaskToPlaceGoer)));
	}
	
	@Override
	public void onOntologize() {
		this.authenticationToPlaceGoer.goTo(new OntologizeInputPlace(), new LinkedList<ToPlaceGoer>());//Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onMatrixGeneration() {
		this.authenticationToPlaceGoer.goTo(new MatrixGenerationInputPlace(), new LinkedList<ToPlaceGoer>());//Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onTreeGeneration() {
		this.authenticationToPlaceGoer.goTo(new TreeGenerationInputPlace(), new LinkedList<ToPlaceGoer>());//Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onTaxonomyComparison() {
		this.authenticationToPlaceGoer.goTo(new TaxonomyComparisonInputPlace(), new LinkedList<ToPlaceGoer>());//Arrays.asList(this.resumeTaskToPlaceGoer)));
	}

	@Override
	public void onVisualization() {
		placeController.goTo(new VisualizationPlace());
	}
	
	@Override
	public void onSample() {
		placeController.goTo(new SamplePlace());
	}

	@Override
	public void onOpenHelpInNewWindow() {
		Window.open("etcsitehelpcenter.html?HelpPlace=" + placeController.getWhere().toString(), "_blank", "");	
	}
	
	@Override
	public void onLoginLogout() {
		if(view.isLogout()) {
			eventBus.fireEvent(new AuthenticationEvent(AuthenticationEventType.LOGGEDOUT));
			Authentication.getInstance().destroy();
		} else if(view.isLogin())
			authenticationPresenter.showLoginWindow();
	}

	private void setLoggedIn() {
		resumableTaskFinder.start();
		view.setLogout();
		
		resumableTasksRegistration = eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEvent.ResumableTasksEventHandler() {
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
