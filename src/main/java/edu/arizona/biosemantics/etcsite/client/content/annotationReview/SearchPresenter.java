package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.common.files.IFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.ISelectableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.common.files.SelectableFileTreePresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileFilter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTreeItem;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.Search;
import edu.arizona.biosemantics.etcsite.shared.model.file.search.SearchResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.IFileServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.search.IFileSearchServiceAsync;

public class SearchPresenter implements ISearchView.Presenter {

	private ISearchView view;
	private Search search;
	private IFileSearchServiceAsync fileSearchService;
	private IFileServiceAsync fileService;
	protected String input;
	private EventBus eventBus;
	private ISelectableFileTreeView.Presenter selectableFileTreePresenter;
	private IFileTreeView.Presenter fileTreePresenter;

	@Inject
	public SearchPresenter(@Named("AnnotationReview")EventBus eventBus, ISearchView view, IFileServiceAsync fileService, IFileSearchServiceAsync fileSearchService, 
			ISelectableFileTreeView.Presenter selectableFileTreePresenter) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		view.setEnabled(false);
		this.fileService = fileService;
		this.fileSearchService = fileSearchService;
		this.fileTreePresenter = selectableFileTreePresenter.getFileTreePresenter();
		this.selectableFileTreePresenter = selectableFileTreePresenter;
	}

	@Override
	public void onSearchButtonClicked(Search search) {
		this.search = search;
		if(search != null) {
			fileSearchService.search(Authentication.getInstance().getToken(), input, search,
					new AsyncCallback<List<SearchResult>>() {
				@Override
				public void onSuccess(List<SearchResult> result) {
					eventBus.fireEvent(new SearchResultEvent(result));
				}
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToSearch(caught);
				}
			});
		}
	}

	@Override
	public void onInputButtonClicked() {
		selectableFileTreePresenter.show("Select File", FileFilter.DIRECTORY, new ISelectListener() {
			@Override
			public void onSelect() {
				List<FileTreeItem> selections = fileTreePresenter.getView().getSelection();
				if(selections.size() == 1) {
					FileTreeItem selection = selections.get(0);
					SearchPresenter.this.input = selection.getFilePath();
					SearchPresenter.this.view.setInput(selection.getFilePath());
					SearchPresenter.this.view.setEnabled(true);
				}
			}
		});
	}

	@Override
	public ISearchView getView() {
		return view;
	}

}
