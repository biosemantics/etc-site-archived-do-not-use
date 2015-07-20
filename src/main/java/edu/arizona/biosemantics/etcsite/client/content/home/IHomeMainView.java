package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.user.client.ui.IsWidget;

public interface IHomeMainView extends IsWidget {

	public interface Presenter {
		void onMatrixGeneration();
		void onSemanticMarkup();
		void onTaxonomyComparison();
		void onVisualization();
		void onPipeline();
		void onTreeGeneration();
		void onOntologize();
	}
	
	void setPresenter(Presenter presenter);

}
