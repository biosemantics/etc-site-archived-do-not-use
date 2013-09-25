package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.FileManagerEvent;
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

	public void go(HasWidgets content, MatrixGenerationConfiguration matrixGenerationConfiguration) {
		this.matrixGenerationConfiguration = matrixGenerationConfiguration;
		matrixGenerationConfiguration.setOutputFile(matrixGenerationConfiguration.getInput() + "_MGResult");
		display.getOutputLabel().setText(matrixGenerationConfiguration.getOutputFile());
		content.clear();
		content.add(display.asWidget());
	}

}
