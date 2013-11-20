package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.event.MatrixGenerationEvent;
import edu.arizona.sirls.etc.site.client.event.SemanticMarkupEvent;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.FileSelectDialogClickHandler;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.ManagableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.SelectableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.ManagableFileTreeView;
import edu.arizona.sirls.etc.site.client.view.fileManager.SelectableFileTreeView;
import edu.arizona.sirls.etc.site.client.view.matrixGeneration.InputMatrixGenerationView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.MatrixGenerationTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.SemanticMarkupTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;

public class InputMatrixGenerationPresenter implements InputMatrixGenerationView.Presenter, Presenter {

	private HandlerManager eventBus;
	private InputMatrixGenerationView view;
	private IMatrixGenerationServiceAsync matrixGenerationService;
	private IFileServiceAsync fileService;
	private StringBuilder inputFile = new StringBuilder();
	private FileSelectDialogClickHandler fileSelectClickHandler;
	
	public InputMatrixGenerationPresenter(HandlerManager eventBus, InputMatrixGenerationView view, IMatrixGenerationServiceAsync matrixGenerationService, 
			IFileServiceAsync fileService) {
		this.eventBus = eventBus;
		this.view = view;
		this.matrixGenerationService = matrixGenerationService;
		this.fileService = fileService;
		view.setPresenter(this);
		fileSelectClickHandler = new FileSelectDialogClickHandler(eventBus, fileService,
				FileFilter.DIRECTORY, 
				view.getInputLabel(), 
				inputFile, true, view.getNextButton());
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void onNext(String taskName, String input) {
		matrixGenerationService.start(Authentication.getInstance().getAuthenticationToken(), 
				taskName, input, new AsyncCallback<RPCResult<MatrixGenerationTaskRun>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(RPCResult<MatrixGenerationTaskRun> result) {
						if(result.isSucceeded())
							eventBus.fireEvent(new MatrixGenerationEvent(result.getData()));
						//ConfigurationManager.getInstance().setMatrixGenerationConfiguration(result);
					}
		});
	}

	@Override
	public void onInputSelect() {
		fileSelectClickHandler.onClick(null);
	}

	@Override
	public void onFileManager() {
		ManagableFileTreeView fileTreeView = new ManagableFileTreeView();
		ManagableFileTreePresenter fileTreePresenter = new ManagableFileTreePresenter(eventBus, 
				fileTreeView, fileService, true, FileFilter.ALL);
		TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(false, "File Manager");
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName("fileManagerScrollPanel");
		scrollPanel.setWidget(fileTreeView);
		dialogBox.setWidget(scrollPanel);
		dialogBox.center();
		dialogBox.setGlassEnabled(true);
 		dialogBox.show();
 		ManagableFileTreePresenter.setInputFileMultiple();
	}

}
