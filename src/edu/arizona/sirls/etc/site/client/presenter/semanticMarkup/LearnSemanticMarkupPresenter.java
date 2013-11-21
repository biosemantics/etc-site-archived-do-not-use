package edu.arizona.sirls.etc.site.client.presenter.semanticMarkup;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
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
import edu.arizona.sirls.etc.site.shared.rpc.semanticMarkup.LearnInvocation;

public class LearnSemanticMarkupPresenter {

	public interface Display {
		void setSentences(int sentences);
		void setWords(int words);
		Widget asWidget();
		Button getNextButton();
		Anchor getTaskManagerAnchor();
		void setResumableStatus();
		void setNonResumableStatus();
		HasClickHandlers getResumableClickable();
	}
	
	private HandlerManager eventBus;
	private Display display;
	private Task task;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private HandlerRegistration taskManagerHandlerRegistration;
	private HandlerRegistration resumableClickableHandlerRegistration;

	public LearnSemanticMarkupPresenter(HandlerManager eventBus,
			final Display display, ISemanticMarkupServiceAsync semanticMarkupService) {
		this.semanticMarkupService = semanticMarkupService;
		this.eventBus = eventBus;
		this.display = display;
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
		setNonResumable();
		
		this.task = task;
		semanticMarkupService.learn(Authentication.getInstance().getAuthenticationToken(),
				task, new AsyncCallback<RPCResult<LearnInvocation>>() { 
			public void onSuccess(RPCResult<LearnInvocation> result) {
				if(result.isSucceeded()) {
					display.setWords(result.getData().getWords());
					display.setSentences(result.getData().getSentences());
					content.clear();
					content.add(display.asWidget());
					loadingPopup.stop();
				}
			}
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
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
