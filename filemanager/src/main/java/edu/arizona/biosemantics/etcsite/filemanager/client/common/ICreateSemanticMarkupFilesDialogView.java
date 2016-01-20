package edu.arizona.biosemantics.etcsite.filemanager.client.common;

import edu.arizona.biosemantics.etcsite.filemanager.client.common.CreateSemanticMarkupFilesDialogPresenter.ICloseHandler;

public interface ICreateSemanticMarkupFilesDialogView {

	public interface Presenter {
		void show(String destinationFilePath);
		void onCancel();
		void setCloseHandler(ICloseHandler closeHandler);
		void onClose();
	}

	void show();

	void hide();

	void setPresenter(ICreateSemanticMarkupFilesDialogView.Presenter presenter);
	
}
