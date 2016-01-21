package edu.arizona.biosemantics.etcsite.client.content.filemanager;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.filemanager.client.common.IManagableFileTreeView;

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
    
    /*@Override
    public void onAnnotationReview() {
        placeController.goTo(new AnnotationReviewPlace());
    }*/

    @Override
    public IsWidget getView() {
        return fileManagerView;
    }
    
    @Override
    public void refresh() {
        managableFileTreePresenter.refresh(null);
    }

}
