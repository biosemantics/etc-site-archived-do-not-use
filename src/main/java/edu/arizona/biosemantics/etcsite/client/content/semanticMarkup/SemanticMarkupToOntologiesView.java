package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SemanticMarkupToOntologiesView extends Composite implements ISemanticMarkupToOntologiesView {

	private static SemanticMarkupToOntologyViewUiBinder uiBinder = GWT
			.create(SemanticMarkupToOntologyViewUiBinder.class);

	interface SemanticMarkupToOntologyViewUiBinder extends
			UiBinder<Widget, SemanticMarkupToOntologiesView> {
	}
	
	@UiField
	SimplePanel toOntologiesContainer;
	
	@UiField
	SimplePanel contextContainer;
	
	private Presenter presenter;

	public SemanticMarkupToOntologiesView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public HasWidgets getToOntologiesContainer() {
		return toOntologiesContainer;
	}
	
	@Override 
	public HasWidgets getContextContainer() {
		return contextContainer;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
}
