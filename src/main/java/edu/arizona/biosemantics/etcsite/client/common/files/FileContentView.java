package edu.arizona.biosemantics.etcsite.client.common.files;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;

public class FileContentView extends Composite implements IFileContentView {

	private static FileContentViewUiBinder uiBinder = GWT.create(FileContentViewUiBinder.class);

	interface FileContentViewUiBinder extends UiBinder<Widget, FileContentView> {
	}

	@UiField(provided=true)
	ListBox formatListBox;
	
	//@UiField
	//Button closeButton;
	
	@UiField
	Button editButton;
	
	@UiField
	Button saveButton;
	
	@UiField
	TextArea textArea;

	private Presenter presenter;
	
	public FileContentView() {
		formatListBox = new ListBox();
		
		for(FileTypeEnum fileType : FileTypeEnum.values())
			if(fileType.isViewable())
				formatListBox.addItem(fileType.displayName());

		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/*@UiHandler("closeButton")
	public void onClose(ClickEvent event) {
		presenter.onClose();
	}*/
	
	@UiHandler("editButton")
	public void onEdit(ClickEvent event) {
		presenter.onEdit();
	}
	
	@UiHandler("saveButton")
	public void onSave(ClickEvent event){
		presenter.onSave();
	}
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("formatListBox")
	public void onFormatChange(ChangeEvent event) {
		presenter.onFormatChange(formatListBox.getItemText(formatListBox.getSelectedIndex()));
	}

	@Override
	public String getFormat() {
		return formatListBox.getItemText(formatListBox.getSelectedIndex());
	}

	@Override
	public void setEditable(boolean enabled){
		this.editButton.setEnabled(!enabled);
		this.textArea.setEnabled(enabled);
		this.saveButton.setEnabled(enabled);
	}
	@Override
	public void setText(String text) {
		this.textArea.setText(text);
	}
	
	@Override
	public String getText() {
		return this.textArea.getText();
	}
}
