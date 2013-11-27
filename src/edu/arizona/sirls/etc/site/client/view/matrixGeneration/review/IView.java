package edu.arizona.sirls.etc.site.client.view.matrixGeneration.review;

import java.util.List;

import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.Matrix;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.Taxon;

public interface IView {

	public interface Presenter {
		void onSave();
	}

	void setPresenter(Presenter presenter);
	void updateTaxon(Taxon taxon);
	void removeTaxon(Taxon taxon);
	void addTaxon(Taxon taxon);
	Taxon getSelectedTaxon();
	void resetSelection();
	void setMatrix(Matrix matrix);

}
