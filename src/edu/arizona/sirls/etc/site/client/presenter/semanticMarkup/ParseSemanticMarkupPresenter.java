package edu.arizona.sirls.etc.site.client.presenter.semanticMarkup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.SemanticMarkupEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEventHandler;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.SemanticMarkupConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.ParseInvocation;

public class ParseSemanticMarkupPresenter {

	public interface Display {
		Button getNextButton();
		Widget asWidget();
		Anchor getTaskManagerAnchor();
		void setResumableStatus();
		HasClickHandlers getResumableClickable();
		void setNonResumableStatus();
	}

	private Display display;
	private HandlerManager eventBus;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private Task task;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private HandlerRegistration taskManagerHandlerRegistration;
	private HandlerRegistration resumableClickableHandlerRegistration;

	public ParseSemanticMarkupPresenter(HandlerManager eventBus,
			Display display, ISemanticMarkupServiceAsync semanticMarkupService) {
		this.eventBus = eventBus;
		this.display = display;
		this.semanticMarkupService = semanticMarkupService;
		bind();
	}

	private void bind() {
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				eventBus.fireEvent(new SemanticMarkupEvent(task));
			}
		});
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(resumableTasksEvent.getTasks().containsKey(task.getId())) {
					setResumable();
				} else {
					setNonResumable();
				}
			}
		});
	}

	public void go(final HasWidgets content, Task task) {
		loadingPopup.start();
		display.setNonResumableStatus();
		this.task = task;
		semanticMarkupService.parse(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<ParseInvocation>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
			@Override
			public void onSuccess(RPCResult<ParseInvocation> result) {		
				content.clear();
				content.add(display.asWidget());
				loadingPopup.stop();
			}
		});
	}
	
	private void setNonResumable() {
		display.setNonResumableStatus();
		taskManagerHandlerRegistration = display.getTaskManagerAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new TaskManagerEvent());
			}
		});
		if(resumableClickableHandlerRegistration != null)
			resumableClickableHandlerRegistration.removeHandler();
	}

	private void setResumable() {
		display.setResumableStatus();
		resumableClickableHandlerRegistration = display.getResumableClickable().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new SemanticMarkupEvent(task));
			}
		});
		if(taskManagerHandlerRegistration != null)
			taskManagerHandlerRegistration.removeHandler();
	}
}
