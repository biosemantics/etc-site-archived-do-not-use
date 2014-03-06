package edu.arizona.biosemantics.etcsite.client.common.files;

public interface ICreateSemanticMarkupFilesDialogView {

	public interface Presenter {
		void hide();
		void show(String destinationFilePath);
	}

	void show();

	void hide();

	void setPresenter(ICreateSemanticMarkupFilesDialogView.Presenter presenter);
	
}
