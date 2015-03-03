package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.search.IFileSearchServiceAsync;

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
		fileFormatService.isValidMarkedupTaxonDescriptionContent(Authentication.getInstance().getToken(), view.getText(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if(result) {
					fileAccessService.setFileContent(Authentication.getInstance().getToken(), target, view.getText(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							Alerter.savedSuccessfully();
						}
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToSetFileContent(caught);
						}
					});
				} else {
					Alerter.invalidFormat();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
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
		
		fileFormatService.isValidMarkedupTaxonDescriptionContent(Authentication.getInstance().getToken(), view.getText(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if(result)
					Alerter.validFormat();
				else
					Alerter.invalidFormat();
			}
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToIsvalidMarkedupTaxonDescriptionContent(caught);
			}
		});
	}
	
	@Override
	public void setTarget(String target) {
		this.setEnabled(true);
		this.target = target;
		fileAccessService.getFileContentHighlighted(Authentication.getInstance().getToken(), target, FileTypeEnum.TAXON_DESCRIPTION, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGetFileContentHighlighted(caught);
			}
			@Override
			public void onSuccess(String result) {
				view.setText(result);
			}
		}); 
	}

	public void setEnabled(boolean value) {
		this.view.setEnabled(value);
	}

	@Override
	public IXMLEditorView getView() {
		return view;
	}
}
