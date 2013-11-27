package edu.arizona.sirls.etc.site.client.presenter.matrixGeneration;

import java.util.List;

import edu.arizona.sirls.etc.site.client.view.matrixGeneration.review.IView;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.Matrix;
import edu.arizona.sirls.etc.site.shared.rpc.matrixGeneration.Taxon;

public class ReviewMatrixPresenter implements IView.Presenter {

	private IView view;

	public ReviewMatrixPresenter(IView view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public void setData(Matrix matrix) {
		view.setMatrix(matrix);
	}
	
	public void onSave() {
		
	}
}
