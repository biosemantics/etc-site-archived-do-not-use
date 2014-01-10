package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;

public class XMLEditorView extends Composite implements IXMLEditorView {

	private static XMLEditorViewUiBinder uiBinder = GWT.create(XMLEditorViewUiBinder.class);

	@UiTemplate("XMLEditorView.ui.xml")
	interface XMLEditorViewUiBinder extends UiBinder<Widget, XMLEditorView> {
	}
	
	@UiField
	//TextArea textArea;
	RichTextArea textArea;
	Presenter presenter;
	@UiField
	Button saveButton;
	@UiField
	Button validateButton;

	public XMLEditorView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setText(String text) {
		//textArea.setText(text);
		textArea.setHTML(text);
	}
	
	@UiHandler("saveButton")
	void onSaveClick(ClickEvent e) {
		if(presenter != null)
			presenter.onSaveButtonClicked();
	}
	
	@UiHandler("validateButton")
	void onValidateClick(ClickEvent e) {
		if(presenter != null)
			presenter.onValidateButtonClicked();
	}

	@Override
	public String getText() {
		String text = textArea.getText();
		//due to the &nbsp; that is in HTML the returned text will contain the no-breaking space (c2 a0) character rather than the space (20) character
		text = text.replace('\u00A0', '\u0020');
		return text;
	}

	@Override
	public void setEnabled(boolean value) {
		this.textArea.setEnabled(value);
		this.saveButton.setEnabled(value);
		this.validateButton.setEnabled(value);
	}

}
