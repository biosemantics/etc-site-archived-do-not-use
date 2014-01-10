package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class SemanticMarkupOutputView extends Composite implements ISemanticMarkupOutputView {

	private static SemanticMarkupOutputViewUiBinder uiBinder = GWT
			.create(SemanticMarkupOutputViewUiBinder.class);

	interface SemanticMarkupOutputViewUiBinder extends
			UiBinder<Widget, SemanticMarkupOutputView> {
	}

	@UiField
	Anchor fileManagerAnchor;
	
	@UiField
	InlineLabel outputLabel;

	private Presenter presenter;
	
	public SemanticMarkupOutputView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("fileManagerAnchor") 
	public void onFileManager(ClickEvent event) {
		presenter.onFileManager();
	}

	@Override
	public void setOutput(String output) {
		this.outputLabel.setText(output);
	}
}
