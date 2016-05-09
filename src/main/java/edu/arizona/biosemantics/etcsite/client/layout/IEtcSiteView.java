package edu.arizona.biosemantics.etcsite.client.layout;

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

		void onOpenHelpInNewWindow();

		void onLoginLogout();

		void updateAuthentication();

		void onSample();

		void onOntologize();
		
		void onGetStarted();

	}
	
	void setContent(IsWidget content);
	void setPresenter(Presenter presenter);
	SimpleLayoutPanel getContentContainer();
	void setNavigationSize(int size, boolean animated);
	void setHelpSize(int size);//, boolean animated);
	AcceptsOneWidget getHelpContainer();
	void setLogin();
	void setLogout();
	boolean isLogin();
	boolean isLogout();
	void setResumableTasks(boolean b);
	void setName(String firstname);
	
}