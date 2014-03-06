package edu.arizona.biosemantics.etcsite.client.common.files;

public interface ICreateSemanticMarkupFilesDialogView {

	public interface Presenter {
		void show();
		void hide();
	}

	void show();

	void hide();

	void setPresenter(ICreateSemanticMarkupFilesDialogView.Presenter presenter);
	
}
