package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

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
