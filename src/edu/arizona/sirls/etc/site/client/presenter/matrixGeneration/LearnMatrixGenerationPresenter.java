package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.TaskManagerEvent;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.ReviewMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.LearnInvocation;

public class LearnMatrixGenerationPresenter {

	public interface Display {
		void setSentences(int sentences);
		void setWords(int words);
		Widget asWidget();
		//Button getNextButton();
		Anchor getTaskManagerAnchor();
		void setResumableStatus();
		void setNonResumableStatus();
		HasClickHandlers getResumableClickable();
	}
	
	private HandlerManager eventBus;
	private Display display;
	private MatrixGenerationConfiguration matrixGenerationConfiguration;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private ITaskServiceAsync taskService;

	public LearnMatrixGenerationPresenter(HandlerManager eventBus,
			final Display display, IMatrixGenerationServiceAsync matrixGenerationService, 
			ITaskServiceAsync taskService) {
		this.matrixGenerationService = matrixGenerationService;
		this.taskService = taskService;
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		display.getTaskManagerAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new TaskManagerEvent());
			}
		});
		/*display.getNextButton().setEnabled(false);
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				eventBus.fireEvent(new ReviewMatrixGenerationEvent());
			}
		});*/ 
	}

	public void go(final HasWidgets content, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		loadingPopup.start();
		display.setNonResumableStatus();
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
		matrixGenerationService.learn(Authentication.getInstance().getAuthenticationToken(),
				matrixGenerationConfiguration, new AsyncCallback<LearnInvocation>() { 
			public void onSuccess(LearnInvocation result) {
				display.setWords(result.getWords());
				display.setSentences(result.getSentences());
				content.clear();
				content.add(display.asWidget());
				loadingPopup.stop();
			}
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
		});
		
		Timer timer = new Timer() {
	        public void run() {
	        	refresh();
	        }
		};
		timer.scheduleRepeating(5000);
	}
	
	private void refresh() {
		taskService.isResumable(Authentication.getInstance().getAuthenticationToken(), 
				matrixGenerationConfiguration.getTask(),
				new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(Boolean isResumable) {
					if(isResumable) {
						display.setResumableStatus();
						display.getResumableClickable().addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								eventBus.fireEvent(new ReviewMatrixGenerationEvent());
							}
						});
					} 
				}
		});
	}

}
