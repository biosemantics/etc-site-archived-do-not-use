package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class OntologizeOutputView extends Composite implements IOntologizeOutputView {

	private static OntologizeOutputViewUiBinder uiBinder = GWT.create(OntologizeOutputViewUiBinder.class);

	interface OntologizeOutputViewUiBinder extends UiBinder<Widget, OntologizeOutputView> {
	}

	private Presenter presenter;

	@UiField
	Anchor fileManagerAnchor;
	
	@UiField
	InlineLabel outputLabel;
	
	public OntologizeOutputView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		fileManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
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
