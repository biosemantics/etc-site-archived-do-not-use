package edu.arizona.sirls.etc.site.client.presenter.semanticMarkup;

import java.io.File;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.Configuration;
import edu.arizona.sirls.etc.site.client.ServerSetup;
import edu.arizona.sirls.etc.site.client.event.FileManagerEvent;
import edu.arizona.sirls.etc.site.client.view.LoadingPopup;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.SemanticMarkupConfiguration;
import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.file.FilePathShortener;

public class OutputSemanticMarkupPresenter {

	public interface Display {
		Widget asWidget();
		Label getOutputLabel();
		Anchor getFileManager();
		void setOutput(String output);
	}

	private Display display;
	private HandlerManager eventBus;
	private IFileServiceAsync fileService;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private Task task;
	private LoadingPopup loadingPopup = new LoadingPopup();
	private FilePathShortener filePathShortener = new FilePathShortener(ServerSetup.getInstance().getSetup().getFileBase(), ServerSetup.getInstance().getSetup().getSeperator());
	private String output;
	
	public OutputSemanticMarkupPresenter(HandlerManager eventBus,
			Display display, IFileServiceAsync fileService, ISemanticMarkupServiceAsync semanticMarkupService) {
		this.eventBus = eventBus;
		this.display = display;
		this.fileService = fileService;
		this.semanticMarkupService = semanticMarkupService;
		bind();
	}

	private void bind() {
		display.getFileManager().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new FileManagerEvent(output));
			}
		});
	}

	public void go(final HasWidgets content, final Task task) {
		loadingPopup.start();
		this.task = task;
		
		

		semanticMarkupService.output(Authentication.getInstance().getAuthenticationToken(), task, new AsyncCallback<RPCResult<Task>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
			@Override
			public void onSuccess(RPCResult<Task> result) {
				output = ((SemanticMarkupConfiguration)result.getData().getConfiguration()).getOutput();
				display.setOutput(filePathShortener.shortenOutput(output, result.getData(), Authentication.getInstance().getUsername()));
				content.clear();
				content.add(display.asWidget());
				loadingPopup.stop();
			}
		});
	}

}
