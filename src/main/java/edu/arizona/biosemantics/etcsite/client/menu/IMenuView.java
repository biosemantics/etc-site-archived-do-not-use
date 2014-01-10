package edu.arizona.biosemantics.etcsite.client.menu;

import com.google.gwt.user.client.ui.IsWidget;

public interface IMenuView extends IsWidget {

	public interface Presenter {
		void onSemanticMarkup();
		void onMatrixGeneration();
		void onTreeGeneration();
		void onTaxonomyComparison();
		void onVisualization();
	}
	
	void setPresenter(Presenter presenter);
	void initNativeJavascriptAnimations();

}
