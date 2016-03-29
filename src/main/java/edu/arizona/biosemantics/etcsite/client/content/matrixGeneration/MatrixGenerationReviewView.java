package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class MatrixGenerationReviewView extends Composite implements IMatrixGenerationReviewView, RequiresResize {

	private static ReviewMatrixGenerationViewUiBinder uiBinder = GWT.create(ReviewMatrixGenerationViewUiBinder.class);

	interface ReviewMatrixGenerationViewUiBinder extends UiBinder<Widget, MatrixGenerationReviewView> {
	}

	private Presenter presenter;

	private MatrixReviewView matrixReviewView = new MatrixReviewView();
	
	@UiField
	SimpleLayoutPanel matrixReviewPanel;
	
	/*@UiField
	Button saveButton;
	
	@UiField
	Button nextButton;*/
	
	@Inject
	public MatrixGenerationReviewView() {
		super();
		/*can not do these, the app won't load. saveButton.setWidth("50");
		nextButton.setWidth("100");*/
		initWidget(uiBinder.createAndBindUi(this));
		matrixReviewPanel.add(matrixReviewView.asWidget());
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("saveButton")
	public void onSave(ClickEvent event) {
		presenter.onSave();
	}

	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
	@UiHandler("exportButton")
	public void onExport(ClickEvent event) {
		presenter.onExport();
	}

	@Override
	public void onResize() {
		((RequiresResize)matrixReviewView).onResize();
	}
	
	@Override
	public MatrixReviewView getMatrixReviewView() {
		return matrixReviewView;
	}

	@Override
	public void setFullModel(Model model) {
		matrixReviewView.setFullModel(model);
	}
}