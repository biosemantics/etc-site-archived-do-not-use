package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public interface IEtcSiteView extends IsWidget {

	public interface Presenter {

		IEtcSiteView getView();

		void onHome();

		void onAbout();

		void onNews();

		void onTaskManager();

		void onFileManager();

		void onAccount();

		void onTextCapture();

		void onMatrixGeneration();

		void onTreeGeneration();

		void onTaxonomyComparison();

		void onVisualization();

	}
	
	void setContent(IsWidget content);
	void setPresenter(Presenter presenter);
	SimpleLayoutPanel getContentContainer();
	void setNavigationSize(int size, boolean animated);
	void setHelpSize(int size, boolean animated);
	AcceptsOneWidget getHelpContainer();
	
}