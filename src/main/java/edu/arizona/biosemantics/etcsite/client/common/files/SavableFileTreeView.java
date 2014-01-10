package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SavableFileTreeView extends Composite implements ISavableFileTreeView {

	private static SavableFileTreeViewUiBinder uiBinder = GWT.create(SavableFileTreeViewUiBinder.class);

	interface SavableFileTreeViewUiBinder extends UiBinder<Widget, SavableFileTreeView> {
	}

	private Presenter presenter;
	
	@UiField
	TextBox nameTextBox;
	
	@UiField
	Button saveButton;
	
	@UiField(provided = true)
	IFileTreeView fileTreeView;
	
	@Inject
	public SavableFileTreeView(IFileTreeView.Presenter fileTreePresenter) {
		this.fileTreeView = fileTreePresenter.getView();
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("saveButton")
	public void onSave(ClickEvent event) {
		presenter.onSave();
	}
	
	public String getName() {
		return nameTextBox.getText();
	}

}
