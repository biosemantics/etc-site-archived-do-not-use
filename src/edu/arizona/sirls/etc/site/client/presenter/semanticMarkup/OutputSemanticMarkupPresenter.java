package edu.arizona.sirls.etc.site.client.presenter.semanticMarkup;

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
import edu.arizona.sirls.etc.site.shared.rpc.ISemanticMarkupServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.SemanticMarkupTaskRun;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.SemanticMarkupConfiguration;

public class OutputSemanticMarkupPresenter {

	public interface Display {
		Widget asWidget();
		Label getOutputLabel();
		Anchor getFileManager();
	}

	private Display display;
	private HandlerManager eventBus;
	private IFileServiceAsync fileService;
	private ISemanticMarkupServiceAsync semanticMarkupService;
	private SemanticMarkupTaskRun semanticMarkupTask;
	private LoadingPopup loadingPopup = new LoadingPopup();
	
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
				eventBus.fireEvent(new FileManagerEvent());
			}
		});
	}

	public void go(final HasWidgets content, final SemanticMarkupTaskRun semanticMarkupTask) {
		loadingPopup.start();
		this.semanticMarkupTask = semanticMarkupTask;
		semanticMarkupService.output(Authentication.getInstance().getAuthenticationToken(), semanticMarkupTask, new AsyncCallback<RPCResult<Void>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				loadingPopup.stop();
			}
			@Override
			public void onSuccess(RPCResult<Void> result) {
				display.getOutputLabel().setText(semanticMarkupTask.getConfiguration().getOutput());
				content.clear();
				content.add(display.asWidget());
				loadingPopup.stop();
			}
		});
	}

}
