package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.inject.Inject;

public class CreateSemanticMarkupFilesDialogPresenter implements ICreateSemanticMarkupFilesDialogView.Presenter {

	private ICreateSemanticMarkupFilesDialogView view;
	private ICreateSemanticMarkupFilesView.Presenter createSemanticMarkupFilesPresenter;

	@Inject
	public CreateSemanticMarkupFilesDialogPresenter(ICreateSemanticMarkupFilesDialogView view, 
			ICreateSemanticMarkupFilesView.Presenter createSemanticMarkupFilesPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.createSemanticMarkupFilesPresenter = createSemanticMarkupFilesPresenter;
	}
	
	@Override
	public void show(String destinationFilePath) {
		createSemanticMarkupFilesPresenter.setDestinationFilePath(destinationFilePath);
		view.show();
	}

	@Override
	public void hide() {
		view.hide();
	}

}
