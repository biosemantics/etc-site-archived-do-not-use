package edu.arizona.sirls.etc.site.client.presenter;

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
import edu.arizona.sirls.etc.site.client.event.FileManagerEvent;
import edu.arizona.sirls.etc.site.client.event.HelpEvent;
import edu.arizona.sirls.etc.site.client.event.HomeEvent;
import edu.arizona.sirls.etc.site.client.event.LogoutEvent;
import edu.arizona.sirls.etc.site.client.event.SettingsEvent;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEvent;
import edu.arizona.sirls.etc.site.client.view.ImageLabelComposite;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;

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

	public LoggedInHeaderPresenter(HandlerManager eventBus, Display display, ITaskServiceAsync taskService) {
		this.taskService = taskService;
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		String username = Authentication.getInstance().getUsername();
		if(username != null)
			this.display.getGreetingLabel().setText("Hello " + username + ":");
		
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
		container.clear();
		refresh();
		container.add(display.asWidget());
		
		Timer timer = new Timer() {
	        public void run() {
	        	refresh();
	        }
		};
		timer.scheduleRepeating(5000);
	}

	private void refresh() {
		taskService.hasResumable(Authentication.getInstance().getAuthenticationToken(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(Boolean result) {
				display.setResumableTaskAvailable(result);
			} 
		});
	}
}
