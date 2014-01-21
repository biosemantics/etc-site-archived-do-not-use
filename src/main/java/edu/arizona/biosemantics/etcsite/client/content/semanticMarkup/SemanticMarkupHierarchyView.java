package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SemanticMarkupHierarchyView extends Composite implements ISemanticMarkupHierarchyView {

	private static SemanticMarkupHierarchyViewUiBinder uiBinder = GWT
			.create(SemanticMarkupHierarchyViewUiBinder.class);

	interface SemanticMarkupHierarchyViewUiBinder extends
			UiBinder<Widget, SemanticMarkupHierarchyView> {
	}
	
	@UiField
	SimplePanel container;

	private Presenter presenter;

	public SemanticMarkupHierarchyView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}

	@Override
	public HasWidgets getHasWidgets() {
		return container;
	}

}
