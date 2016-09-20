package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

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

import edu.arizona.biosemantics.euler.alignment.client.EulerAlignmentView;

public class TaxonomyComparisonAlignView extends Composite implements ITaxonomyComparisonAlignView, RequiresResize {

	private static TaxonomyComparisonRunViewUiBinder uiBinder = GWT.create(TaxonomyComparisonRunViewUiBinder.class);

	interface TaxonomyComparisonRunViewUiBinder extends UiBinder<Widget, TaxonomyComparisonAlignView> {
	}

	private Presenter presenter;

	@UiField
	SimpleLayoutPanel eulerPanel;
	
	private EulerAlignmentView eulerAlignmentView = new EulerAlignmentView();
	
	@Inject
	public TaxonomyComparisonAlignView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		eulerPanel.setWidget(eulerAlignmentView.asWidget());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void onResize() {
		((RequiresResize)eulerAlignmentView).onResize();
	}

	@Override
	public EulerAlignmentView getEulerAlignmentView() {
		return eulerAlignmentView;
	}

	@UiHandler("saveButton")
	public void onSave(ClickEvent event) {
		presenter.onSave();
	}
}
