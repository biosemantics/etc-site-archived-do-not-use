package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;

public interface ISemanticMarkupCreateView extends IsWidget{
	
	public interface Presenter {

		IsWidget getView();

		void onNext();

		void onInput();

		void onFileManager();

		void getAllFolders();
		
	}
	
	void setPresenter(Presenter presenter);

	void setFolderNames(List<FileInfo> result);

}
