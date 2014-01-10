
package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.user.client.ui.IsWidget;

public interface IHomeContentView extends IsWidget {

	public interface Presenter {
		void onMatrixGeneration();
		void onSemanticMarkup();
		void onTaxonomyComparison();
		void onVisualization();
		void onPipeline();
		void onTreeGeneration();
	}
	
	void setPresenter(Presenter presenter);

}
