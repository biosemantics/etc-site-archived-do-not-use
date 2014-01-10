package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.etcsite.shared.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCResult;

public class XMLEditorPresenter implements IXMLEditorView.Presenter {

	private final IXMLEditorView view;
	private IFileAccessServiceAsync fileAccessService;
	private IFileFormatServiceAsync fileFormatService;
	private IFileSearchServiceAsync fileSearchService;
	private String target;
	private EventBus eventBus;
	
	@Inject
	public XMLEditorPresenter(@Named("AnnotationReview")EventBus eventBus, IXMLEditorView view, IFileAccessServiceAsync fileAccessService, IFileFormatServiceAsync fileFormatService, 
			IFileSearchServiceAsync fileSearchService) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		this.fileAccessService = fileAccessService;
		this.fileFormatService = fileFormatService;
		this.fileSearchService = fileSearchService;
		
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
		fileFormatService.isValidMarkedupTaxonDescriptionContent(new AuthenticationToken("test", ""), view.getText(), new RPCCallback<Boolean>() {
			@Override
			public void onResult(Boolean result) {
				if(result) {
					fileAccessService.setFileContent(new AuthenticationToken("test", ""), target, view.getText(), new RPCCallback<Void>() {
						@Override
						public void onResult(Void result) {
							Window.alert("Saved successfully");
						}
					});
				} else {
					Window.alert("Invalid format. Can't save.");
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

	public void setEnabled(boolean value) {
		this.view.setEnabled(value);
	}
}
