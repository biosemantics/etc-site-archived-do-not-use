package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.IMatrixGenerationReviewView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.model.Task;
import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public interface IReviewView extends IsWidget, RequiresResize {

	public interface Presenter {

		void refresh(Task task);
		IReviewView getView();
		TaxonMatrix getTaxonMatrix();
		
	}

	void setMatrixReviewView(MatrixReviewView matrixReviewView);
	void setPresenter(Presenter presenter);
	Widget asWidget();
}

