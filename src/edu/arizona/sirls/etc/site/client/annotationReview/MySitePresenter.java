package edu.arizona.sirls.etc.site.client.annotationReview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.annotationReview.presenter.AnnotationReviewPresenter;
import edu.arizona.sirls.etc.site.client.annotationReview.presenter.ResultPresenter;
import edu.arizona.sirls.etc.site.client.annotationReview.presenter.SearchPresenter;
import edu.arizona.sirls.etc.site.client.annotationReview.presenter.XMLEditorPresenter;
import edu.arizona.sirls.etc.site.client.annotationReview.view.AnnotationReviewView;
import edu.arizona.sirls.etc.site.client.annotationReview.view.AnnotationReviewViewImpl;
import edu.arizona.sirls.etc.site.client.annotationReview.view.ResultView;
import edu.arizona.sirls.etc.site.client.annotationReview.view.ResultViewImpl;
import edu.arizona.sirls.etc.site.client.annotationReview.view.SearchView;
import edu.arizona.sirls.etc.site.client.annotationReview.view.SearchViewImpl;
import edu.arizona.sirls.etc.site.client.annotationReview.view.XMLEditorView;
import edu.arizona.sirls.etc.site.client.annotationReview.view.XMLEditorViewImpl;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.IMatrixGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaskServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITaxonomyComparisonService;
import edu.arizona.sirls.etc.site.shared.rpc.ITaxonomyComparisonServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.ITreeGenerationService;
import edu.arizona.sirls.etc.site.shared.rpc.ITreeGenerationServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IVisualizationService;
import edu.arizona.sirls.etc.site.shared.rpc.IVisualizationServiceAsync;

public class MySitePresenter implements Presenter, ValueChangeHandler<String> {

	private HandlerManager eventBus;
	private HasWidgets container;
	
	private final IAuthenticationServiceAsync authenticationService = GWT.create(IAuthenticationService.class);
	private final IFileServiceAsync fileService = GWT.create(IFileService.class);
	private final IFileFormatServiceAsync fileFormatService = GWT.create(IFileFormatService.class);
	private final IFileAccessServiceAsync fileAccessService = GWT.create(IFileAccessService.class);
	private final IFileSearchServiceAsync fileSearchService = GWT.create(IFileSearchService.class);
	private final ITaskServiceAsync taskService = GWT.create(ITaskService.class);
	private final IMatrixGenerationServiceAsync matrixGenerationService = GWT.create(IMatrixGenerationService.class);
	private final ITreeGenerationServiceAsync treeGenerationService = GWT.create(ITreeGenerationService.class);
	private final ITaxonomyComparisonServiceAsync taxonomyComparisonService = GWT.create(ITaxonomyComparisonService.class);
	private final IVisualizationServiceAsync visualizationService = GWT.create(IVisualizationService.class);

	private SearchPresenter searchPresenter;
	private ResultPresenter resultPresenter;
	private XMLEditorPresenter xmlEditorPresenter;
	private AnnotationReviewPresenter annotationReviewPresenter;

	public MySitePresenter(HandlerManager eventBus) {
		this.eventBus = eventBus;
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);

		/*eventBus.addHandler(AddContactEvent.TYPE, new AddContactEventHandler() {
			public void onAddContact(AddContactEvent event) {
				doAddNewContact();
			}
		}); */
	}

	/*private void doAddNewContact() {
		History.newItem("add");
	}

	private void doEditContact(String id) {
		History.newItem("edit", false);
	} */

	public void go(final HasWidgets container) {
		this.container = container;

		if ("".equals(History.getToken())) {
			History.newItem(HistoryState.annotationReview.toString());
		} else {
			History.fireCurrentHistoryState();
		}
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if (token != null) {
			Presenter presenter = null;
			if (token.equals("annotationReview")) {
				if(annotationReviewPresenter == null) {
					SearchViewImpl searchView = new SearchViewImpl();
					searchPresenter = new SearchPresenter(eventBus, searchView, fileService, fileSearchService);
					ResultViewImpl resultView = new ResultViewImpl();
					resultPresenter = new ResultPresenter(eventBus, resultView);
					XMLEditorViewImpl xmlEditorView = new XMLEditorViewImpl();
					xmlEditorPresenter = new XMLEditorPresenter(eventBus, xmlEditorView, fileAccessService, fileFormatService, fileSearchService);
					AnnotationReviewViewImpl annotationReviewView = new AnnotationReviewViewImpl(searchView, resultView, xmlEditorView, fileAccessService, fileFormatService, fileSearchService);
					annotationReviewPresenter = new AnnotationReviewPresenter(eventBus, annotationReviewView);
				}
				presenter = annotationReviewPresenter;
			} 

			if (presenter != null) {
				presenter.go(container);
			}
		}
	}

}
