package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.db.Task;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Matrix;
import edu.arizona.biosemantics.etcsite.shared.rpc.matrixGeneration.Taxon;

public interface IReviewView extends IsWidget {

	public interface Presenter {
		void onSave();
		void refresh(Task task);
		IReviewView getView();
	}

	void setPresenter(Presenter presenter);
	void updateTaxon(Taxon taxon);
	void removeTaxon(Taxon taxon);
	void addTaxon(Taxon taxon);
	Taxon getSelectedTaxon();
	void resetSelection();
	void setMatrix(Matrix matrix);
	Matrix getMatrix();
	Widget asWidget();

}
