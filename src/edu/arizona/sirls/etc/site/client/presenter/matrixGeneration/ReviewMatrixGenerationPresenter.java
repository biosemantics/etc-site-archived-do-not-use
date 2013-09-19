package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.matrixGeneration.ParseMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJob;

public class ReviewMatrixGenerationPresenter {

	public interface Display {
		Anchor getOtoAnchor();
		Button getNextButton();
		Widget asWidget();
	}

	private HandlerManager eventBus;
	private Display display;
	private MatrixGenerationJob matrixGenerationJob;
	
	public ReviewMatrixGenerationPresenter(HandlerManager eventBus,
			Display display) {
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		display.getOtoAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(true, "Term Review");
				Frame frame = new Frame(matrixGenerationJob.getReviewTermsLink());
			    frame.addStyleName("otoFrame");
			    dialogBox.setWidget(frame);
			    dialogBox.setGlassEnabled(true); 
			    dialogBox.center(); 
			    dialogBox.show(); 
			}
		});
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				eventBus.fireEvent(new ParseMatrixGenerationEvent());
			}
		});
	}

	public void go(HasWidgets content, MatrixGenerationJob matrixGenerationJob) {
		this.matrixGenerationJob = matrixGenerationJob;
		matrixGenerationJob.setReviewTermsLink("http://biosemantics.arizona.edu:8080/OTOLite/?uploadID=54");
		content.clear();
		content.add(display.asWidget());
	}

}
