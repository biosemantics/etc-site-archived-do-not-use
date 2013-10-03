package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class ReviewMatrixGenerationPresenter {

	public interface Display {
		Frame getFrame();
		Button getNextButton();
		Widget asWidget();
	}

	private HandlerManager eventBus;
	private Display display;
	private MatrixGenerationConfiguration matrixGenerationConfiguration;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	public ReviewMatrixGenerationPresenter(HandlerManager eventBus, Display display, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.display = display;
		this.matrixGenerationService = matrixGenerationService;
		bind();
	}

	private void bind() {		
		display.getNextButton().addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) { 
				nextStep();
			}
		});
		initIFrameMessaging();
	}
	
	public void nextStep() {
		eventBus.fireEvent(new MatrixGenerationEvent(matrixGenerationConfiguration));
	}

	public void go(final HasWidgets content, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		loadingPopup.start();
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
		matrixGenerationService.review(Authentication.getInstance().getAuthenticationToken(), matrixGenerationConfiguration, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
			@Override
			public void onSuccess(String result) {
				display.getFrame().setUrl(result);
				content.clear();
				content.add(display.asWidget());
				loadingPopup.stop();
			}
		});
	}
	
	public native void initIFrameMessaging() /*-{
		var thisPresenterReference = this;
		$wnd.onmessage = function(e) {
		    if (e.data == 'done') {
		    	//if simply this is used here, the reference in javascript will then be the $wnd object rather than the java 'this'. 
		    	//Therefore the reference is passed above already
		        thisPresenterReference.@edu.arizona.sirls.etc.site.client.presenter.matrixGeneration.ReviewMatrixGenerationPresenter::nextStep()();
		    }
		}
	}-*/;


}
