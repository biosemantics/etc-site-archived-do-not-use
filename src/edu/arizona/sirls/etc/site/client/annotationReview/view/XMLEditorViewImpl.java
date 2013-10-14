package edu.arizona.sirls.etc.site.client.annotationReview.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class XMLEditorViewImpl extends Composite implements XMLEditorView {

	private static XMLEditorViewUiBinder uiBinder = GWT.create(XMLEditorViewUiBinder.class);

	@UiTemplate("XMLEditorView.ui.xml")
	interface XMLEditorViewUiBinder extends UiBinder<Widget, XMLEditorViewImpl> {
	}
	
	@UiField
	//TextArea textArea;
	RichTextArea textArea;
	Presenter presenter;
	@UiField
	Button saveButton;
	@UiField
	Button validateButton;

	public XMLEditorViewImpl() {
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
		return this.textArea.getText();
	}

	@Override
	public void setEnabled(boolean value) {
		this.textArea.setEnabled(value);
		this.saveButton.setEnabled(value);
		this.validateButton.setEnabled(value);
	}

}
