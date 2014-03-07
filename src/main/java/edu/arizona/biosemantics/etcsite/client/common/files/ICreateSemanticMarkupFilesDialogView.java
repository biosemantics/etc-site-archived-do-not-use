package edu.arizona.biosemantics.etcsite.client.common.files;

import edu.arizona.biosemantics.etcsite.client.common.files.CreateSemanticMarkupFilesDialogPresenter.ICloseHandler;

public interface ICreateSemanticMarkupFilesDialogView {

	public interface Presenter {
		void hide();
		void show(String destinationFilePath);
		void onCancel();
		void setCloseHandler(ICloseHandler closeHandler);
	}

	void show();

	void hide();

	void setPresenter(ICreateSemanticMarkupFilesDialogView.Presenter presenter);
	
}
