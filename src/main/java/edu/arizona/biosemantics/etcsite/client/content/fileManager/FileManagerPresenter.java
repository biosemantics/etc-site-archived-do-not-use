package edu.arizona.biosemantics.etcsite.client.content.fileManager;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.arizona.biosemantics.etcsite.client.common.files.IManagableFileTreeView;
import edu.arizona.biosemantics.etcsite.client.content.annotationReview.AnnotationReviewPlace;
import edu.arizona.biosemantics.etcsite.shared.file.FileFilter;

public class FileManagerPresenter implements IFileManagerView.Presenter {

	private PlaceController placeController;
	private IFileManagerView fileManagerView;
	private IManagableFileTreeView.Presenter managableFileTreePresenter;
	
	@Inject
	public FileManagerPresenter(PlaceController placeController, 
			IFileManagerView fileManagerView, 
			IManagableFileTreeView.Presenter managableFileTreePresenter) {
		super();
		this.placeController = placeController;
		this.fileManagerView = fileManagerView;
		fileManagerView.setPresenter(this);
		this.managableFileTreePresenter = managableFileTreePresenter;
	}
	
	@Override
	public void onAnnotationReview() {
		placeController.goTo(new AnnotationReviewPlace());
	}

	@Override
	public IsWidget getView() {
		return fileManagerView;
	}
	
	@Override
	public void refresh() {
		managableFileTreePresenter.refresh(FileFilter.ALL);
	}

}
