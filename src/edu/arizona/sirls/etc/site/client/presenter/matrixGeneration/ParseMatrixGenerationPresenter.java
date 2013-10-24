package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

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
import edu.arizona.sirls.etc.site.client.event.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEvent;
import edu.arizona.sirls.etc.site.client.event.ResumableTasksEventHandler;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.ParseInvocation;

public class ParseMatrixGenerationPresenter {

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
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private MatrixGenerationTaskRun matrixGenerationTask;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private HandlerRegistration taskManagerHandlerRegistration;
	private HandlerRegistration resumableClickableHandlerRegistration;

	public ParseMatrixGenerationPresenter(HandlerManager eventBus,
			Display display, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.display = display;
		this.matrixGenerationService = matrixGenerationService;
		bind();
	}

	private void bind() {
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				eventBus.fireEvent(new MatrixGenerationEvent(matrixGenerationTask));
			}
		});
		eventBus.addHandler(ResumableTasksEvent.TYPE, new ResumableTasksEventHandler() {
			@Override
			public void onResumableTaskEvent(ResumableTasksEvent resumableTasksEvent) {
				if(resumableTasksEvent.getTasks().containsKey(matrixGenerationTask.getTask().getId())) {
					setResumable();
				} else {
					setNonResumable();
				}
			}
		});
	}

	public void go(final HasWidgets content, MatrixGenerationTaskRun matrixGenerationTask) {
		loadingPopup.start();
		display.setNonResumableStatus();
		this.matrixGenerationTask = matrixGenerationTask;
		matrixGenerationService.parse(Authentication.getInstance().getAuthenticationToken(), matrixGenerationTask, new AsyncCallback<ParseInvocation>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
			@Override
			public void onSuccess(ParseInvocation result) {		
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
				eventBus.fireEvent(new MatrixGenerationEvent(matrixGenerationTask));
			}
		});
		if(taskManagerHandlerRegistration != null)
			taskManagerHandlerRegistration.removeHandler();
	}
}
