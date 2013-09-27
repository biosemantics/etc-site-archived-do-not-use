package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.FileManagerEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.db.MatrixGenerationConfiguration;

public class OutputMatrixGenerationPresenter {

	public interface Display {
		Widget asWidget();
		Label getOutputLabel();
		Anchor getFileManager();
	}

	private Display display;
	private HandlerManager eventBus;
	private IFileServiceAsync fileService;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private MatrixGenerationConfiguration matrixGenerationConfiguration;
	private LoadingPopup loadingPopup = new LoadingPopup();
	
	public OutputMatrixGenerationPresenter(HandlerManager eventBus,
			Display display, IFileServiceAsync fileService, IMatrixGenerationServiceAsync matrixGenerationService) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
		this.matrixGenerationService = matrixGenerationService;
		bind();
	}

	private void bind() {
		display.getFileManager().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new FileManagerEvent());
			}
		});
	}

	public void go(final HasWidgets content, final MatrixGenerationConfiguration matrixGenerationConfiguration) {
		loadingPopup.start();
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
		matrixGenerationConfiguration.setOutput(matrixGenerationConfiguration.getInput() + "_MGResult");
		matrixGenerationService.output(Authentication.getInstance().getAuthenticationToken(), matrixGenerationConfiguration, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
			@Override
			public void onSuccess(Boolean result) {
				display.getOutputLabel().setText(matrixGenerationConfiguration.getOutput());
				content.clear();
				content.add(display.asWidget());
				loadingPopup.stop();
			}
		});
	}

}
