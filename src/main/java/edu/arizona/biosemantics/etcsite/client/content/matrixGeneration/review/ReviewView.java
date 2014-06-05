package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.user.client.ui.SimpleLayoutPanel;

import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;

/**
 * MatrixReviewView is a RequiresResize (delegates to SplitLayoutPanel/DockLayoutPanel), hence use ResizeLayoutPanel which is a ProvidesResize
 * @author rodenhausen
 */
public class ReviewView extends SimpleLayoutPanel implements IReviewView {

	private Presenter presenter;
	
	@Override
	public void setMatrixReviewView(MatrixReviewView matrixReviewView) {
		this.setWidget(matrixReviewView.asWidget());
		matrixReviewView.forceLayout();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}	
}
