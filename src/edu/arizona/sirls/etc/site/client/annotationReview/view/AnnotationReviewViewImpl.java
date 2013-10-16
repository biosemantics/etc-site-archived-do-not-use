package edu.arizona.sirls.etc.site.client.annotationReview.view;

import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
 
public class AnnotationReviewViewImpl extends Composite implements AnnotationReviewView {
 
    private static AnnotationReviewViewUiBinder uiBinder = GWT.create(AnnotationReviewViewUiBinder.class);

	@UiTemplate("AnnotationReviewView.ui.xml")
	interface AnnotationReviewViewUiBinder extends UiBinder<Widget, AnnotationReviewViewImpl> {
	}
	
    @UiField(provided=true)
    SearchViewImpl search;
    @UiField(provided=true)
    ResultViewImpl result;    
    @UiField(provided=true)
    XMLEditorViewImpl xmlEditor;
    
	private Presenter presenter;

    public AnnotationReviewViewImpl(SearchViewImpl search, ResultViewImpl result, XMLEditorViewImpl xmlEditor,
    		IFileAccessServiceAsync fileAccessService, IFileFormatServiceAsync fileFormatService, IFileSearchServiceAsync fileSearchService) {
        this.search = search;
        this.result = result;
        this.xmlEditor = xmlEditor;
    	initWidget(uiBinder.createAndBindUi(this));
    }

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}