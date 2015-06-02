package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationCreateView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import gwtupload.client.Uploader;

public interface ITaxonomyComparisonCreateView extends IsWidget {

	public interface Presenter {
		IsWidget getView();
		
		String getInputFolderPath();
		
		String getInputFolderShortenedPath();
		
		void onFileManager();

		void refresh();
	}
	  
	public void setPresenter(Presenter presenter);
	
	public IInputCreateView getInputCreateView();
	

}
