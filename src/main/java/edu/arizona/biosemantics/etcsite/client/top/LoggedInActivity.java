package edu.arizona.biosemantics.etcsite.client.top;

import java.util.Map;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.content.fileManager.FileManagerPlace;
import edu.arizona.biosemantics.etcsite.client.content.help.HelpPlace;
import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.client.content.news.NewsPlace;
import edu.arizona.biosemantics.etcsite.client.content.settings.SettingsPlace;
import edu.arizona.biosemantics.etcsite.client.content.taskManager.TaskManagerPlace;
import edu.arizona.biosemantics.etcsite.client.event.ResumableTasksEvent;
import edu.arizona.biosemantics.etcsite.client.top.ITopView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.task.ITaskServiceAsync;

public class LoggedInActivity implements Activity, Presenter {

	private PlaceController placeController;
	private ITopView topView;
	private Timer resumableTasksTimer;
	private int resumableTasksTime;
	private IAuthenticationServiceAsync authenticationService;

	@Inject
	public LoggedInActivity(final ITopView topView, PlaceController placeController, 
			final ITaskServiceAsync taskService, @Named("Tasks") final EventBus tasksBus, @Named("CheckResumables")int resumableTasksTime, 
			IAuthenticationServiceAsync authenticationService) {
		this.topView = topView;
		this.placeController = placeController;
		this.resumableTasksTime = resumableTasksTime;
		this.resumableTasksTimer = new Timer() {
	        public void run() {
	        	taskService.getResumableTasks(Authentication.getInstance().getToken(), new AsyncCallback<Map<Integer, Task>>() {
					@Override
					public void onSuccess(Map<Integer, Task> result) {
						topView.setResumableTasks(!result.isEmpty());
						tasksBus.fireEvent(new ResumableTasksEvent(result));
					}
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToGetResumableTasks(caught);
					}
	    		});
	        }
		};
		this.authenticationService = authenticationService;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
		topView.setPresenter(this);
		panel.setWidget(topView.asWidget());
		this.resumableTasksTimer.scheduleRepeating(resumableTasksTime);
		
		String name = Authentication.getInstance().getFirstName();
		topView.setGreeting("Welcome " + name + "!");
	}
		
	@Override
	public String mayStop() {
		return null;
	}

	@Override
	public void onCancel() {
		if(resumableTasksTimer != null)
			resumableTasksTimer.cancel();
	}

	@Override
	public void onStop() {
		if(resumableTasksTimer != null)
			resumableTasksTimer.cancel();
	}

	@Override
	public void onFileManager() {
		placeController.goTo(new FileManagerPlace());
	}

	@Override
	public void onTaskManager() {
		placeController.goTo(new TaskManagerPlace());
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
	public void onSettings() {
		placeController.goTo(new SettingsPlace());
	}

	@Override
	public void onHelp() {
		placeController.goTo(new HelpPlace());
	}

	@Override
	public void onLogout() {
		Authentication.getInstance().destroy();
		placeController.goTo(new LoggedOutPlace());
		Alerter.signOutSuccessful();
	}
}
