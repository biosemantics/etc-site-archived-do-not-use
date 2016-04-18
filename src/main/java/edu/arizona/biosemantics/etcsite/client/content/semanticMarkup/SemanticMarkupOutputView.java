package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
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
	Button sendToOtoButton;
	
	@UiField
	InlineLabel outputLabel;
	
	@UiField
	InlineLabel outputLabelTermReview;

	private Presenter presenter;

	private String outputFull;
	
	public SemanticMarkupOutputView() {
		initWidget(uiBinder.createAndBindUi(this));
		fileManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
		
		sendToOtoButton.setTitle("Contribute classifications to OTO");
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
	public void setOutput(String outputFull, String outputFullDisplay, String outputTermReview) {
		this.outputFull = outputFull;
		this.outputLabel.setText(outputFullDisplay);
		this.outputLabelTermReview.setText(outputTermReview);
	}
	
	@UiHandler("continueMatrixGenerationButton")
	public void onMatrixGeneration(ClickEvent event) {
		presenter.onContinueMatrixGeneration(outputFull);
	}

	@UiHandler("sendToOtoButton")
	public void onSendToOto(ClickEvent event) {
		presenter.onSendToOto();
	}

	@Override
	public void setEnabledSendToOto(boolean value) {
		sendToOtoButton.setEnabled(value);
	}
}
