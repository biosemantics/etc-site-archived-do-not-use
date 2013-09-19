package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.matrixGeneration.OutputMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJob;

public class ParseMatrixGenerationPresenter {

	public interface Display {
		Button getNextButton();
		Widget asWidget();
	}

	private Display display;
	private HandlerManager eventBus;
	private MatrixGenerationJob matrixGenerationJob;

	public ParseMatrixGenerationPresenter(HandlerManager eventBus,
			Display display) {
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				eventBus.fireEvent(new OutputMatrixGenerationEvent());
			}
		});
	}

	public void go(HasWidgets content, MatrixGenerationJob matrixGenerationJob) {
		this.matrixGenerationJob = matrixGenerationJob;
		content.clear();
		content.add(display.asWidget());
	}

}
