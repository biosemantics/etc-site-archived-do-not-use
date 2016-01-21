package edu.arizona.biosemantics.etcsite.client.content.filemanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.filemanager.client.common.IManagableFileTreeView;

public class FileManagerView extends Composite implements IFileManagerView {

	private static FileManagerViewUiBinder uiBinder = GWT.create(FileManagerViewUiBinder.class);

	interface FileManagerViewUiBinder extends UiBinder<Widget, FileManagerView> {
	}

	private Presenter presenter;
	
	@UiField(provided = true)
	IManagableFileTreeView managableFileTreeView;
	
	//@UiField
	//Anchor annotationReviewAnchor;
	
	@Inject
	public FileManagerView(IManagableFileTreeView.Presenter managableFileTreePresenter) {
		this.managableFileTreeView = managableFileTreePresenter.getView();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	/*@UiHandler("annotationReviewAnchor")
	public void onAnnotationReview(ClickEvent event) {
		presenter.onAnnotationReview();
	}*/

}
