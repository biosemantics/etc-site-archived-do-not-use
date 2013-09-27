package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.matrixGeneration.OutputMatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.ParseInvocation;

public class ParseMatrixGenerationPresenter {

	public interface Display {
		Button getNextButton();
		Widget asWidget();
	}

	private Display display;
	private HandlerManager eventBus;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private MatrixGenerationConfiguration matrixGenerationConfiguration;
	private LoadingPopup loadingPopup = new LoadingPopup();

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
				eventBus.fireEvent(new OutputMatrixGenerationEvent());
			}
		});
	}

	public void go(final HasWidgets content, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		loadingPopup.start();
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
		matrixGenerationService.parse(Authentication.getInstance().getAuthenticationToken(), matrixGenerationConfiguration, new AsyncCallback<ParseInvocation>() {
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

}
