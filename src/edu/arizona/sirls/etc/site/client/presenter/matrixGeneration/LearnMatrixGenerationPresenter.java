package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.ReviewMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.LearnInvocation;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJob;

public class LearnMatrixGenerationPresenter {

	public interface Display {
		void setSentences(int sentences);
		void setWords(int words);
		Widget asWidget();
		Button getNextButton();
	}
	
	private HandlerManager eventBus;
	private Display display;
	private MatrixGenerationJob matrixGenerationJob;
	private IMatrixGenerationServiceAsync matrixGenerationService;

	public LearnMatrixGenerationPresenter(HandlerManager eventBus,
			final Display display, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.matrixGenerationService = matrixGenerationService;
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				eventBus.fireEvent(new ReviewMatrixGenerationEvent());
			}
		}); 
	}

	public void go(HasWidgets content, MatrixGenerationJob matrixGenerationJob) {
		this.matrixGenerationJob = matrixGenerationJob;
		matrixGenerationService.learn(Authentication.getInstance().getAuthenticationToken(),
				matrixGenerationJob, new AsyncCallback<LearnInvocation>() { 
			public void onSuccess(LearnInvocation result) {
				display.setWords(result.getWords());
				display.setSentences(result.getSentences());
			}
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
		content.clear();
		content.add(display.asWidget());
	}

}
