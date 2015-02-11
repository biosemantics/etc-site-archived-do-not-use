package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class TaxonomyComparisonReviewView extends Composite implements ITaxonomyComparisonReviewView, RequiresResize {

	private static TaxonomyComparisonViewUiBinder uiBinder = GWT.create(TaxonomyComparisonViewUiBinder.class);

	interface TaxonomyComparisonViewUiBinder extends UiBinder<Widget, TaxonomyComparisonReviewView> {
	}

	private Presenter presenter;

	private MatrixReviewView matrixReviewView = new MatrixReviewView();
	
	/*@UiField
	Button saveButton;
	
	@UiField
	Button nextButton;*/
	
	@Inject
	public TaxonomyComparisonReviewView() {
		super();
		/*can not do these, the app won't load. saveButton.setWidth("50");
		nextButton.setWidth("100");*/
		initWidget(uiBinder.createAndBindUi(this));
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void onResize() {
		((RequiresResize)matrixReviewView).onResize();
	}
	
}