package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.shared.rpc.file.access.IFileAccessServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.format.IFileFormatServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.search.IFileSearchServiceAsync;
 
public class AnnotationReviewView extends Composite implements IAnnotationReviewView {
 
    private static AnnotationReviewViewUiBinder uiBinder = GWT.create(AnnotationReviewViewUiBinder.class);

	@UiTemplate("AnnotationReviewView.ui.xml")
	interface AnnotationReviewViewUiBinder extends UiBinder<Widget, AnnotationReviewView> {
	}
	
    @UiField(provided=true)
    ISearchView search;
    @UiField(provided=true)
    IResultView result;    
    @UiField(provided=true)
    IXMLEditorView xmlEditor;
    
	private Presenter presenter;

	@Inject
    public AnnotationReviewView(ISearchView.Presenter searchPresenter, IResultView.Presenter resultPresenter, IXMLEditorView.Presenter xmlEditorPresenter,
    		IFileAccessServiceAsync fileAccessService, IFileFormatServiceAsync fileFormatService, IFileSearchServiceAsync fileSearchService) {
    	this.search = searchPresenter.getView();
        this.result = resultPresenter.getView();
        this.xmlEditor = xmlEditorPresenter.getView();
    	initWidget(uiBinder.createAndBindUi(this));
    }

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}