package edu.arizona.sirls.etc.site.client.presenter.annotationReview;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.presenter.annotationReview.events.TargetEvent;
import edu.arizona.sirls.etc.site.client.presenter.annotationReview.events.TargetEventHandler;
import edu.arizona.sirls.etc.site.client.view.annotationReview.XMLEditorView;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileTypeEnum;
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
		fileFormatService.isValidMarkedupTaxonDescriptionContent(new AuthenticationToken("test", ""), view.getText(), new AsyncCallback<RPCResult<Boolean>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(RPCResult<Boolean> result) {
				if(result.isSucceeded()) {
					if(result.getData()) {
						fileAccessService.setFileContent(new AuthenticationToken("test", ""), target, view.getText(), new AsyncCallback<RPCResult<Void>>() {
							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}
							@Override
							public void onSuccess(RPCResult<Void> result) {
								Window.alert("Saved successfully");
							}
						});
					} else {
						Window.alert("Invalid format. Can't save.");
					}
				}
			}
		});
	}

	@Override
	public void onValidateButtonClicked() {
		/*byte[] bytes = view.getText().getBytes();
	    StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	    	 sb.append(Integer.toHexString(b & 0xFF) + " ");
	    }
	    System.out.println(sb.toString());*/
		
		fileFormatService.isValidMarkedupTaxonDescriptionContent(new AuthenticationToken("test", ""), view.getText(), new AsyncCallback<RPCResult<Boolean>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(RPCResult<Boolean> result) {
				if(result.isSucceeded()) {
					if(result.getData())
						Window.alert("Valid format");
					else
						Window.alert("Invalid format");
				}
			}
		});
	}
	
	@Override
	public void setTarget(String target) {
		this.setEnabled(true);
		this.target = target;
		fileAccessService.getFileContentHighlighted(new AuthenticationToken("test", ""), target, FileTypeEnum.TAXON_DESCRIPTION, new AsyncCallback<RPCResult<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			@Override
			public void onSuccess(RPCResult<String> result) {
				if(result.isSucceeded())
					view.setText(result.getData());
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
