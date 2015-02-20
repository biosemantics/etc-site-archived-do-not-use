package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationViewView;
import edu.arizona.biosemantics.etcsite.client.content.treeGeneration.ITreeGenerationViewView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.euler.alignment.client.EulerAlignmentView;
import edu.ucdavis.cs.cfgproject.client.KeyView;

public interface IProcessingView extends IsWidget {

	public interface IOnTaskManagerListener {
		public void onTaskManager();
	}
	
	public interface Presenter {
		IProcessingView getView();

		void onTaskManager();

		void addOnTaskManagerListener(IOnTaskManagerListener listener);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	
}
