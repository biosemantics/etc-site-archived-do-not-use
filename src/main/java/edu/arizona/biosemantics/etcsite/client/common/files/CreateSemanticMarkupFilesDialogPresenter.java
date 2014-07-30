package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.inject.Inject;

public class CreateSemanticMarkupFilesDialogPresenter implements ICreateSemanticMarkupFilesDialogView.Presenter {

	private ICreateSemanticMarkupFilesDialogView view;
	private ICreateSemanticMarkupFilesView.Presenter createSemanticMarkupFilesPresenter;
	private ICloseHandler closeHandler;

	public interface ICloseHandler {
		public void onClose(int filesCreated);
	}
	
	@Inject
	public CreateSemanticMarkupFilesDialogPresenter(ICreateSemanticMarkupFilesDialogView view, 
			ICreateSemanticMarkupFilesView.Presenter createSemanticMarkupFilesPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.createSemanticMarkupFilesPresenter = createSemanticMarkupFilesPresenter;
	}
	
	@Override
	public void show(String destinationFilePath) {
		createSemanticMarkupFilesPresenter.init();
		createSemanticMarkupFilesPresenter.setDestinationFilePath(destinationFilePath);
		view.show();
	}

	@Override
	public void onCancel() {
		view.hide();
	}
	
	@Override
	public void setCloseHandler(ICloseHandler closeHandler) {
		this.closeHandler = closeHandler;
	}

	@Override
	public void onClose() {
		closeHandler.onClose(createSemanticMarkupFilesPresenter.getFilesCreated());
	}

}
