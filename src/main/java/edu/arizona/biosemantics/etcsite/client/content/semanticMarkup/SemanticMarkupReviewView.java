package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class SemanticMarkupReviewView extends Composite implements ISemanticMarkupReviewView {

	private static SemanticMarkupReviewViewUiBinder uiBinder = GWT
			.create(SemanticMarkupReviewViewUiBinder.class);

	interface SemanticMarkupReviewViewUiBinder extends
			UiBinder<Widget, SemanticMarkupReviewView> {
	}

	private Presenter presenter;
	
	@UiField
	Button nextButton;
	
	@UiField
	Frame frame;

	public SemanticMarkupReviewView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		this.presenter.onNext();
	}

	@Override
	public void setFrameUrl(String url) {
		this.frame.setUrl(url);
	}

}
