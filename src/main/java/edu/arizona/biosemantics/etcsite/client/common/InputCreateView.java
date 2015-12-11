package edu.arizona.biosemantics.etcsite.client.common;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import gwtupload.client.Uploader;

public class InputCreateView extends Composite implements IInputCreateView {

	private static SemanticmarkupCreateViewUiBinder uiBinder = GWT
			.create(SemanticmarkupCreateViewUiBinder.class);

	interface SemanticmarkupCreateViewUiBinder extends UiBinder<Widget, InputCreateView> {
	}

	private IInputCreateView.Presenter presenter;
	private ListStore<FileInfo> ownedFoldersStore;
	
	@UiField VerticalPanel verticalPanel;
	
	@UiField RadioButton createFilesRadio;
	@UiField VerticalPanel createPanel;
	@UiField VerticalPanel dummyCreatePanel;
	@UiField RadioButton createFolderForCreateFilesRadio;
	@UiField Button createFolderForCreateFilesButton;
	@UiField TextBox createFolderForCreateFilesTextBox;
	@UiField RadioButton selectFolderForCreateFilesRadio;
	@UiField(provided=true) ComboBox<FileInfo> selectFolderForCreateFilesComboBox;
	@UiField Button createFilesButton;
	@UiField RadioButton dummyCreateFilesRadio;
	
	@UiField RadioButton uploadRadio;
	@UiField VerticalPanel uploadPanel;
	@UiField RadioButton createFolderForUploadRadio;
	@UiField Button createFolderForUploadButton;
	@UiField TextBox createFolderForUploadTextBox;
	@UiField RadioButton selectFolderForUploadRadio;
	@UiField(provided=true) ComboBox<FileInfo> selectFolderForUploadComboBox;
	@UiField Button uploadButton;
	@UiField Uploader uploader;
	@UiField SimplePanel statusWidgetContainer;
	
	@UiField RadioButton selectExistingFolderRadio;
	@UiField HorizontalPanel selectPanel;	
	@UiField Button selectExistingFolderButton;
	@UiField Label selectExistingFolderLabel;
	
	@UiField Button nextButton;	
	
	public InputCreateView() {
		ownedFoldersStore = new ListStore<FileInfo>(new ModelKeyProvider<FileInfo>() {
			@Override
			public String getKey(FileInfo item) {
				return item.getName(false);
			}
	    });
	    LabelProvider<FileInfo> nameLabelProvider = new LabelProvider<FileInfo>() {
			@Override
			public String getLabel(FileInfo item) {
				return item.getName(false);
			}
	    };
	    selectFolderForCreateFilesComboBox = new ComboBox<FileInfo>(ownedFoldersStore, nameLabelProvider);
	    selectFolderForCreateFilesComboBox.setTriggerAction(TriggerAction.ALL);
		selectFolderForUploadComboBox = new ComboBox<FileInfo>(ownedFoldersStore, nameLabelProvider);
		selectFolderForUploadComboBox.setTriggerAction(TriggerAction.ALL);
		selectFolderForCreateFilesComboBox.setEnabled(false);
		selectFolderForUploadComboBox.setEnabled(false);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		createFolderForCreateFilesTextBox.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
		createFolderForUploadTextBox.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
		
		verticalPanel.remove(0);
		verticalPanel.remove(0);
	}

	@Override
	public void setPresenter(IInputCreateView.Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("createFilesRadio")
	public void onCreateRadio(ClickEvent event){
		createPanel.setVisible(true);
		dummyCreatePanel.setVisible(false);
		uploadPanel.setVisible(false);
		selectPanel.setVisible(false);
		createFolderForUploadTextBox.setValue(null);
	}
	
	@UiHandler("dummyCreateFilesRadio")
	public void onDummyCreateRadio(ClickEvent event){
		createPanel.setVisible(false);
		dummyCreatePanel.setVisible(true);
		uploadPanel.setVisible(false);
		selectPanel.setVisible(false);
		createFolderForUploadTextBox.setValue(null);
	}
	
	@UiHandler("uploadRadio")
	public void onUploadRadio(ClickEvent event){
		createPanel.setVisible(false);
		dummyCreatePanel.setVisible(false);
		uploadPanel.setVisible(true);
		selectPanel.setVisible(false);
		createFolderForCreateFilesTextBox.setValue(null);
	}
	
	@UiHandler("selectExistingFolderRadio")
	public void onSelectRadio(ClickEvent event){
		createPanel.setVisible(false);
		dummyCreatePanel.setVisible(false);
		uploadPanel.setVisible(false);
		selectPanel.setVisible(true);
		createFolderForCreateFilesTextBox.setValue(null);
		createFolderForUploadTextBox.setValue(null);
	}
	
	@UiHandler("createFolderForCreateFilesRadio")
	public void onCreateFolderForCreateFilesRadio(ClickEvent event){
		createFolderForCreateFilesTextBox.setEnabled(true);
		selectFolderForCreateFilesComboBox.setEnabled(false);
		createFilesButton.setEnabled(true);
		createFolderForCreateFilesButton.setVisible(true);
		createFilesButton.setText("Create Files in New Folder");
		createFolderForUploadTextBox.setValue(null);
	}
	
	@UiHandler("selectFolderForCreateFilesRadio")
	public void onSelectFolderForCreateFilesRadio(ClickEvent event){
		createFolderForCreateFilesTextBox.setEnabled(false);
		selectFolderForCreateFilesComboBox.setEnabled(true);
		createFolderForCreateFilesButton.setVisible(false);
		createFilesButton.setEnabled(true);
		createFilesButton.setText("Create Files in Selected Folder");
		createFolderForCreateFilesTextBox.setValue(null);
	}
	
	@UiHandler("createFolderForUploadRadio")
	public void onCreateFolderForUploadRadio(ClickEvent event){
		createFolderForUploadTextBox.setEnabled(true);
		selectFolderForUploadComboBox.setEnabled(false);
		uploadButton.setEnabled(true);
		uploadButton.setText("Upload Files in New Folder");
		createFolderForUploadButton.setVisible(true);
		createFolderForCreateFilesTextBox.setValue(null);
	}
	
	@UiHandler("selectFolderForUploadRadio")
	public void onSelectFolderRadioUpload(ClickEvent event){
		createFolderForUploadTextBox.setEnabled(false);
		selectFolderForUploadComboBox.setEnabled(true);
		createFolderForUploadButton.setVisible(false);
		uploadButton.setEnabled(true);
		uploadButton.setText("Upload Files in Selected Folder");
		createFolderForUploadTextBox.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
	}
	
	@UiHandler("createFilesButton")
	public void onCreateFiles(ClickEvent event){
		if(this.isSelectFolderForCreateFiles()) {
			presenter.createFiles(selectFolderForCreateFilesComboBox.getValue());
		} else if(this.isCreateFolderForCreateFiles()) {
			presenter.createFilesInNewFolder();
		}
	}
	
	@UiHandler("createFolderForCreateFilesButton")
	public void onCreateNewFolderButton_create(ClickEvent event){
		String folderName = createFolderForCreateFilesTextBox.getText();
		presenter.createNewFolder(folderName);
	}
	
	@UiHandler("createFolderForUploadButton")
	public void onCreateNewFolderButton_upload(ClickEvent event){
		String folderName = createFolderForUploadTextBox.getText();
		presenter.createNewFolder(folderName);
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
	@UiHandler("selectExistingFolderButton")
	public void onSelectFolder(ClickEvent event) {
		presenter.onSelectExistingFolder();
	}
	
	@Override
	public void setOwnedFolders(List<FileInfo> folders){
		ownedFoldersStore.replaceAll(folders);
	}
	
	@Override
	public boolean isCreateFiles() {
		return createFilesRadio.getValue();
	}
	
	@Override
	public Uploader getUploader() {
		return uploader;
	}

	@Override
	public Button getUploadButton() {
		return uploadButton;
	}

	@Override
	public void setStatusWidget(Widget widget) {
		statusWidgetContainer.setWidget(widget);
	}

	@Override
	public boolean isSelectExistingFolder() {
		return selectExistingFolderRadio.getValue();
	}
	
	@Override
	public boolean isCreateFolderForCreateFiles() {
		return createFolderForCreateFilesRadio.getValue();
	}
	
	@Override
	public boolean isCreateFolderForUpload() {
		return createFolderForUploadRadio.getValue();
	}
	
	@Override
	public boolean isSelectFolderForUpload() {
		return selectFolderForUploadRadio.getValue();
	}

	@Override
	public FileInfo getSelectedFolderForCreateFiles() {
		return selectFolderForCreateFilesComboBox.getValue();
	}

	@Override
	public FileInfo getSelectedFolderForUpload() {
		return selectFolderForUploadComboBox.getValue();
	}

	@Override
	public void setSelectedExistingFolder(String shortendPath) {
		selectExistingFolderLabel.setText(shortendPath);
	}
	
	@Override
	public void setNextButtonText(String text) {
		this.nextButton.setText(text);
	}

	@Override
	public boolean isUpload() {
		return uploadRadio.getValue();
	}

	@Override
	public void removeCreateFiles() {
		verticalPanel.remove(0);
		verticalPanel.remove(0);
	}
	
	@Override
	public void addDummyCreateFiles() {
		verticalPanel.insert(dummyCreatePanel, 0);
		verticalPanel.insert(dummyCreateFilesRadio, 0);
	}

	@Override
	public boolean isSelectFolderForCreateFiles() {
		return this.selectFolderForCreateFilesRadio.getValue();
	}

	@Override
	public void setNextButtonName(String str) {
		nextButton.setText(str);
	}
	
}
