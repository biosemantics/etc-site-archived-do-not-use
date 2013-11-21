package edu.arizona.sirls.etc.site.client.presenter;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.TaskManager;
import edu.arizona.sirls.etc.site.client.event.FileManagerEvent;
import edu.arizona.sirls.etc.site.client.event.HelpEvent;
import edu.arizona.sirls.etc.site.client.event.HomeEvent;
import edu.arizona.sirls.etc.site.client.event.LogoutEvent;
import edu.arizona.sirls.etc.site.client.event.LogoutEventHandler;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEventHandler;
import edu.arizona.sirls.etc.site.client.event.SettingsEvent;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEvent;
import edu.arizona.sirls.etc.site.client.view.ImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;

public class LoggedInHeaderPresenter implements Presenter {

	private HandlerManager eventBus;
	public interface Display {
		Label getGreetingLabel();
		Button getLogoutButton();
		ImageLabelComposite getHelpImageLabelComposite();
		ImageLabelComposite getHomeImageLabelComposite();
		ImageLabelComposite getTaskManagerImageLabelComposite();
		ImageLabelComposite getFileManagerImageLabelComposite();
		ImageLabelComposite getSettingsImageLabelComposite();
		Widget asWidget();
		void setResumableTaskAvailable(boolean value);
	}
	private final Display display;
	private ITaskServiceAsync taskService;
	private TaskManager taskManager;

	public LoggedInHeaderPresenter(HandlerManager eventBus, Display display, ITaskServiceAsync taskService, TaskManager taskManager) {
		this.taskService = taskService;
		this.taskManager = taskManager;
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {
			public void onResumableTaskEvent(ResumableTasksEvent event) {
				if(taskManager != null) {
					if(taskManager.hasActiveTask()) {
						Task activeTask = taskManager.getActiveTask();
						if(event.getTasks().containsKey(activeTask.getId())) {
							display.setResumableTaskAvailable(event.getTasks().size() > 1);
						}
					} else {
						display.setResumableTaskAvailable(!event.getTasks().isEmpty());
					}
				} else 
					display.setResumableTaskAvailable(false);
			}
		});
		
				
		this.display.getLogoutButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new LogoutEvent());
			}
		});
		this.display.getHelpImageLabelComposite().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new HelpEvent());
			}
		});
		this.display.getHomeImageLabelComposite().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new HomeEvent());
			}
		});
		this.display.getSettingsImageLabelComposite().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new SettingsEvent());
			}
		});
		this.display.getTaskManagerImageLabelComposite().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new TaskManagerEvent());
			}
		});
		this.display.getFileManagerImageLabelComposite().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new FileManagerEvent());
			}
		});
	}

	@Override
	public void go(final HasWidgets container) {
		String username = Authentication.getInstance().getUsername();
		if(username != null)
			this.display.getGreetingLabel().setText("Hello " + username + ":");
		
		container.clear();
		container.add(display.asWidget());
	}

}
