package edu.arizona.sirls.etc.site.client.annotationReview.presenter;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.client.annotationReview.Presenter;
import edu.arizona.sirls.etc.site.client.annotationReview.view.AnnotationReviewView;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.SelectableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.view.fileManager.SelectableFileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public class AnnotationReviewPresenter implements Presenter, AnnotationReviewView.Presenter {

	private AnnotationReviewView view;
	private HandlerManager eventBus;
	private IFileServiceAsync fileService;
	private IFileAccessServiceAsync fileAccessService;
	private IFileSearchServiceAsync fileSearchService;
	protected String input;
	private Search search;

	public AnnotationReviewPresenter(HandlerManager eventBus, AnnotationReviewView view, IFileServiceAsync fileService, IFileAccessServiceAsync fileAccessService, 
			IFileSearchServiceAsync fileSearchService) {
		this.eventBus = eventBus;
		this.view = view;
		this.fileService = fileService;
		this.fileAccessService = fileAccessService;
		this.fileSearchService = fileSearchService;
		this.view.setPresenter(this);
		this.view.setEnabledSearch(false);
		this.view.setEnabledXMLEditor(false);
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void onSearchButtonClicked(Search search) {
		this.search = search;
		if(search != null) {
			fileSearchService.search(new AuthenticationToken("test", ""), input, search, new AsyncCallback<List<SearchResult>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(List<SearchResult> searchResults) {
					view.setResult(searchResults);
				}
			});
		}
	}

	@Override
	public void onInputButtonClicked() {
		final TitleCloseDialogBox dialogBox = new TitleCloseDialogBox(true, "Select File");
		SelectableFileTreeView view = new SelectableFileTreeView();
		final SelectableFileTreePresenter presenter = new SelectableFileTreePresenter(eventBus, view, fileService, FileFilter.DIRECTORY);
		presenter.setSelectClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String target = presenter.getFileSelectionHandler().getTarget();
				if(target != null) {
					AnnotationReviewPresenter.this.input = target;
					AnnotationReviewPresenter.this.view.setInput(target);
					AnnotationReviewPresenter.this.view.setEnabledSearch(true);
				}
				dialogBox.hide();
			}
		});
		presenter.setCloseClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		presenter.getFileTreePresenter().refresh();
		dialogBox.setWidget(view);
		dialogBox.setAnimationEnabled(true);
		dialogBox.setGlassEnabled(true);
		dialogBox.center();
	 	dialogBox.show(); 
	}

	@Override
	public void onResultClicked(SearchResult searchResult) {
		if(searchResult != null) {
			view.setEnabledXMLEditor(true);
			view.getXMLEditorPresenter().setTarget(this.search, searchResult.getTarget());
		}
	}
}
