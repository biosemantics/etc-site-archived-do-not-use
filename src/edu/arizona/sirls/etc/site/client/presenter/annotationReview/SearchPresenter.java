package edu.arizona.sirls.etc.site.client.presenter.annotationReview;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;

import edu.arizona.sirls.etc.site.client.presenter.annotationReview.events.SearchResultEvent;
import edu.arizona.sirls.etc.site.client.presenter.fileManager.SelectableFileTreePresenter;
import edu.arizona.sirls.etc.site.client.view.annotationReview.SearchView;
import edu.arizona.sirls.etc.site.client.view.fileManager.FileImageLabelTreeItem;
import edu.arizona.sirls.etc.site.client.view.fileManager.SelectableFileTreeView;
import edu.arizona.sirls.etc.site.shared.rpc.AuthenticationToken;
import edu.arizona.sirls.etc.site.shared.rpc.IFileSearchServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.IFileServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.file.FileFilter;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.Search;
import edu.arizona.sirls.etc.site.shared.rpc.file.search.SearchResult;

public class SearchPresenter implements SearchView.Presenter {

	private SearchView view;
	private HandlerManager eventBus;
	private Search search;
	private IFileSearchServiceAsync fileSearchService;
	private IFileServiceAsync fileService;
	protected String input;

	public SearchPresenter(HandlerManager eventBus, SearchView view, IFileServiceAsync fileService, IFileSearchServiceAsync fileSearchService) {
		this.eventBus = eventBus;
		this.view = view;
		this.fileService = fileService;
		this.fileSearchService = fileSearchService;
		view.setPresenter(this);
		view.setEnabled(false);
	}

	@Override
	public void onSearchButtonClicked(Search search) {
		this.search = search;
		if(search != null) {
			fileSearchService.search(new AuthenticationToken("test", ""), input, search,
					new AsyncCallback<RPCResult<List<SearchResult>>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(RPCResult<List<SearchResult>> result) {
					if(result.isSucceeded())
						eventBus.fireEvent(new SearchResultEvent(result.getData()));
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
				FileImageLabelTreeItem selection = presenter.getFileSelectionHandler().getSelection();
				if(selection != null) {
					SearchPresenter.this.input = selection.getFileInfo().getFilePath();
					SearchPresenter.this.view.setInput(selection.getFileInfo().getFilePath());
					SearchPresenter.this.view.setEnabled(true);
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


}
