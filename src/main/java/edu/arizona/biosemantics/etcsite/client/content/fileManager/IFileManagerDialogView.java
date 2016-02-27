package edu.arizona.biosemantics.etcsite.client.content.fileManager;

public interface IFileManagerDialogView {

	public interface Presenter {
		void show();
		void hide();
	}

	void show();

	void hide();
	
}
