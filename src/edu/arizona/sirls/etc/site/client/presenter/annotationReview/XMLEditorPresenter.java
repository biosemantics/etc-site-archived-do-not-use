package edu.arizona.sirls.etc.site.client.presenter.annotationReview;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.presenter.annotationReview.events.TargetEvent;
import edu.arizona.sirls.etc.site.client.presenter.annotationReview.events.TargetEventHandler;
import edu.arizona.sirls.etc.site.client.view.annotationReview.XMLEditorView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileType;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;


public class XMLEditorPresenter implements Presenter, XMLEditorView.Presenter {

	private final XMLEditorView view;
	private IFileAccessServiceAsync fileAccessService;
	private IFileFormatServiceAsync fileFormatService;
	private IFileSearchServiceAsync fileSearchService;
	private String target;
	private HandlerManager eventBus;
	
	public XMLEditorPresenter(HandlerManager eventBus, XMLEditorView view, IFileAccessServiceAsync fileAccessService, IFileFormatServiceAsync fileFormatService, 
			IFileSearchServiceAsync fileSearchService) {
		this.eventBus = eventBus;
		this.view = view;
		this.fileAccessService = fileAccessService;
		this.fileFormatService = fileFormatService;
		this.fileSearchService = fileSearchService;
		view.setPresenter(this);
		
		this.setEnabled(false);		
		eventBus.addHandler(TargetEvent.TYPE, new TargetEventHandler() {
			@Override
			public void onTarget(TargetEvent targetEvent) {
				XMLEditorPresenter.this.setTarget(targetEvent.getTarget());
			}
		});
	}
	
	@Override
	public void onSaveButtonClicked() {
		fileAccessService.setFileContent(new AuthenticationToken("test", ""), target, view.getText(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(Boolean result) {
			}
		});
	}

	@Override
	public void onValidateButtonClicked() {
		fileFormatService.isValidMarkedupTaxonDescription(new AuthenticationToken("test", ""), target, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(Boolean result) {
				if(result)
					Window.alert("Valid format");
				else
					Window.alert("Invalid format");
			}
		});
		view.getText();
	}
	
	@Override
	public void setTarget(String target) {
		this.setEnabled(true);
		this.target = target;
		fileAccessService.getFileContentHighlighted(new AuthenticationToken("test", ""), target, FileType.TAXON_DESCRIPTION, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(String result) {
				view.setText(result);
			}
		});
	}

	@Override
	public void go(HasWidgets container) {
	    container.clear();
	    container.add(view.asWidget());
	}

	public void setEnabled(boolean value) {
		this.view.setEnabled(value);
	}
}
