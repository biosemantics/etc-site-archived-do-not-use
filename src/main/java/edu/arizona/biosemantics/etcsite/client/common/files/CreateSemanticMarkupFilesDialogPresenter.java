package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.inject.Inject;

public class CreateSemanticMarkupFilesDialogPresenter implements ICreateSemanticMarkupFilesDialogView.Presenter {

	private ICreateSemanticMarkupFilesDialogView view;
	private ICreateSemanticMarkupFilesView.Presenter presenter;

	@Inject
	public CreateSemanticMarkupFilesDialogPresenter(ICreateSemanticMarkupFilesDialogView view, 
			ICreateSemanticMarkupFilesView.Presenter presenter) {
		this.view = view;
		view.setPresenter(this);
		this.presenter = presenter;
	}
	
	@Override
	public void show() {
		view.show();
	}

	@Override
	public void hide() {
		view.hide();
	}

}
