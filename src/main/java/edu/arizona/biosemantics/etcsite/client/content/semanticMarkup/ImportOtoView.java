package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class ImportOtoView extends Composite implements IImportOtoView {

	private static ImportOtoViewUiBinder uiBinder = GWT.create(ImportOtoViewUiBinder.class);
	
	interface ImportOtoViewUiBinder extends UiBinder<Widget, ImportOtoView> { }
	
	private Dialog dialog;
	
	@UiField
	TextArea categorizationTextArea;
	
	@UiField
	TextArea synonymTextArea;
		
	private IImportOtoView.Presenter presenter;

	public ImportOtoView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("Import Term Categorizations");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(false);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		TextButton okButton = dialog.getButton(PredefinedButton.OK);
		okButton.setText("Import");
		dialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				presenter.onImport(categorizationTextArea.getText(), synonymTextArea.getText());
			}
		});
		dialog.add(this);
	}

	@Override
	public void setPresenter(IImportOtoView.Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public void show() {
		dialog.show();
	}

	@Override
	public void hide() {
		dialog.hide();
	}
}
