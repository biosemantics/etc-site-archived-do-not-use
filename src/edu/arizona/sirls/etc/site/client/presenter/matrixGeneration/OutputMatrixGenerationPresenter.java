package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.event.HomeEvent;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.SavableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.view.fileManager.SavableFileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationJob;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class OutputMatrixGenerationPresenter {

	public interface Display {
		Widget asWidget();
		Button getSelectButton();
		Button getCompleteButton();
		Label getFileLabel();
	}

	private Display display;
	private HandlerManager eventBus;
	private MatrixGenerationJob matrixGenerationJob;
	private IFileServiceAsync fileService;
	
	public OutputMatrixGenerationPresenter(HandlerManager eventBus,
			Display display, IFileServiceAsync fileService) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
		bind();
	}

	private void bind() {
		display.getCompleteButton().setEnabled(false);
		display.getSelectButton().addClickHandler(new ClickHandler() {

			private TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(true, "Save");
			private SavableFileTreeView view = new SavableFileTreeView();
			private SavableFileTreePresenter presenter = new SavableFileTreePresenter(eventBus, view, fileService, FileFilter.DIRECTORY,
					new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							String target = presenter.getFileSelectionHandler().getTarget();
							String fileName = view.getNameTextBox().getText();
							String outputFile = target + "//" + fileName;
							display.getFileLabel().setText(outputFile);
							matrixGenerationJob.setOutputFile(outputFile);
							display.getCompleteButton().setEnabled(true);
							dialogBox.hide();
						} 
					});
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.setWidget(view);
				dialogBox.setAnimationEnabled(true);
				dialogBox.setGlassEnabled(true);
				dialogBox.center();
		 		dialogBox.show(); 
			}
			
		});
		display.getCompleteButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new HomeEvent());
			}
		});
	}

	public void go(HasWidgets content, MatrixGenerationJob matrixGenerationJob) {
		this.matrixGenerationJob = matrixGenerationJob;
		content.clear();
		content.add(display.asWidget());
	}

}
