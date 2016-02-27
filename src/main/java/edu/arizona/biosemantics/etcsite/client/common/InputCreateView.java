package edu.arizona.biosemantics.etcsite.client.common;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
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

	private static InputCreateViewUiBinder uiBinder = GWT.create(InputCreateViewUiBinder.class);

	interface InputCreateViewUiBinder extends UiBinder<Widget, InputCreateView> {
	}

	private IInputCreateView.Presenter presenter;
	private ListStore<FileInfo> ownedFoldersStore;
	
	@UiField VerticalPanel verticalPanel;
	
	@UiField RadioButton createFilesRadio;
	@UiField VerticalPanel createPanel;
	@UiField VerticalPanel dummyCreatePanel1;
	@UiField VerticalPanel dummyCreatePanel2;
	@UiField RadioButton createFolderForCreateFilesRadio;
	@UiField Button createFolderForCreateFilesButton;
	@UiField TextBox createFolderForCreateFilesTextBox;
	@UiField RadioButton selectFolderForCreateFilesRadio;
	@UiField(provided=true) ComboBox<FileInfo> selectFolderForCreateFilesComboBox;
	@UiField Button createFilesButton;
	@UiField RadioButton dummyCreateFilesRadio1;
	@UiField RadioButton dummyCreateFilesRadio2;
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
	
	@UiField
	Grid fileGrid;

	
	@UiField TextBox fileDirectory;
	
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
		selectFolderForUploadComboBox.addSelectionHandler(new SelectionHandler<FileInfo>() {
            @Override
            public void onSelection (SelectionEvent<FileInfo> event){
        			activiateuploadButton2();       
            }
		});	     
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
		dummyCreatePanel1.setVisible(false);
		dummyCreatePanel2.setVisible(false);
		resetUpload();
		resetSelectExisting();

	}
	
	@UiHandler("dummyCreateFilesRadio1")
	public void onDummyCreateRadio1(ClickEvent event){
		createPanel.setVisible(false);
		dummyCreatePanel1.setVisible(true);
		resetUpload();
		resetSelectExisting();

	}
	
	@UiHandler("dummyCreateFilesRadio2")
	public void onDummyCreateRadio2(ClickEvent event){
		createPanel.setVisible(false);
		dummyCreatePanel2.setVisible(true);
		resetUpload();
		resetSelectExisting();
	}
	
	@UiHandler("uploadRadio")
	public void onUploadRadio(ClickEvent event){
		uploadPanel.setVisible(true);
		resetCreate();
		resetSelectExisting();
		
	}
	
	@UiHandler("selectExistingFolderRadio")
	public void onSelectRadio(ClickEvent event){
		selectPanel.setVisible(true);
		selectExistingFolderButton.setVisible(true);
		resetCreate();
		resetUpload();

	}
	
	@UiHandler("createFolderForCreateFilesRadio")
	public void onCreateFolderForCreateFilesRadio(ClickEvent event){
		createFolderForCreateFilesTextBox.setEnabled(true);
		selectFolderForCreateFilesComboBox.setEnabled(false);
		createFilesButton.setEnabled(true);
		createFolderForCreateFilesButton.setVisible(true);
		createFilesButton.setText("Create Files in New Folder");
		createFolderForUploadTextBox.setValue(null);
		selectFolderForUploadComboBox.setText(null);
		selectFolderForUploadComboBox.setValue(null);
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
		createFolderForUploadTextBox.setValue(null);
		createFolderForUploadButton.setVisible(true);
		selectFolderForUploadComboBox.setText(null);
		selectFolderForUploadComboBox.setValue(null);
		enableuploadButton();
	}
	
	@UiHandler("selectFolderForUploadRadio")
	public void onSelectFolderRadioUpload(ClickEvent event){
		createFolderForUploadTextBox.setEnabled(false);
		selectFolderForUploadComboBox.setEnabled(true);
		createFolderForUploadButton.setVisible(false);
		createFolderForUploadTextBox.setValue(null);
		enableuploadButton();
	}
	

	
	
	@UiHandler("createFilesButton")
	public void onCreateFiles(ClickEvent event){
		if(this.isSelectFolderForCreateFiles()) {
			presenter.createFiles(this.selectFolderForCreateFilesComboBox.getValue());
		} else if(this.isCreateFolderForCreateFiles()) {
			presenter.createFilesInNewFolder();
		}
		else Alerter.inputError("Please create or select a folder first!");
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
		activiateuploadButton1();
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	

	@UiHandler("selectExistingFolderButton")
	public void onSelectFolder(ClickEvent event) {
		presenter.onSelectExistingFolder();
	}
	
	
	private void activiateuploadButton1() {
		uploadButton.setEnabled(true);
		uploadButton.setText("Upload Files in New Folder");
	}
	
	private void activiateuploadButton2() {
		uploadButton.setEnabled(true);
		uploadButton.setText("Upload Files in Existing Folder");
	}
	
	private void enableuploadButton() {
		uploadButton.setEnabled(false);
		uploadButton.setText("Please select or Create a folder first");
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
		fileDirectory.setText(shortendPath);
		fileDirectory.setVisible(true);
		fileGrid.setWidget(0, 0, fileDirectory);
		fileGrid.setWidget(0,1,selectExistingFolderButton);
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
	public void removeDummyCreateFiles1() {
		verticalPanel.remove(0);
		verticalPanel.remove(0);

	}
	
	@Override
	public void removeDummyCreateFiles2() {
		verticalPanel.remove(0);
		verticalPanel.remove(0);
	}
	
	@Override
	public void addDummyCreateFiles1() {

		verticalPanel.insert(dummyCreatePanel1, 0);
		verticalPanel.insert(dummyCreateFilesRadio1, 0);
	}
	
	@Override
	public void addDummyCreateFiles2() {
		verticalPanel.insert(dummyCreatePanel2, 0);
		verticalPanel.insert(dummyCreateFilesRadio2, 0);
	}

	@Override
	public boolean isSelectFolderForCreateFiles() {
		return this.selectFolderForCreateFilesRadio.getValue();
	}

	@Override
	public void setNextButtonName(String str) {
		nextButton.setText(str);
	}
	
	@Override
	public void resetCreate(){
		createFilesRadio.setChecked(false);
		createFolderForCreateFilesRadio.setChecked(false);
		selectFolderForCreateFilesRadio.setChecked(false);
		createFolderForCreateFilesTextBox.setValue(null);
		createFolderForCreateFilesTextBox.setEnabled(false);
		createFolderForCreateFilesButton.setVisible(false);
		selectFolderForCreateFilesComboBox.setValue(null);
		selectFolderForCreateFilesComboBox.setText(null);
		selectFolderForCreateFilesComboBox.setEnabled(false);
		createFilesButton.setText("Create files");
		createPanel.setVisible(false);
		dummyCreateFilesRadio1.setChecked(false);
		dummyCreatePanel1.setVisible(false);
		dummyCreateFilesRadio2.setChecked(false);
		dummyCreatePanel2.setVisible(false);
		presenter.deleteFolderForinputFiles();
		enableuploadButton();
	}
	
	@Override
	public void resetUpload(){
		uploadRadio.setChecked(false);
		createFolderForUploadRadio.setChecked(false);
		selectFolderForUploadRadio.setChecked(false);
		createFolderForUploadTextBox.setValue(null);
		createFolderForUploadTextBox.setEnabled(false);
		createFolderForUploadButton.setVisible(false);
		selectFolderForUploadComboBox.setValue(null);
		selectFolderForUploadComboBox.setText(null);
		selectFolderForUploadComboBox.setEnabled(false);
		enableuploadButton();
		uploadPanel.setVisible(false);
		presenter.deleteFolderForinputFiles();
	}
	
	@Override
	public void resetSelectExisting(){
		selectExistingFolderRadio.setChecked(false);
		selectPanel.setVisible(false);
		selectExistingFolderButton.setVisible(false);
		fileDirectory.setValue(null);
		fileDirectory.setVisible(false);
		fileDirectory.setEnabled(false);
		presenter.deleteFolderForinputFiles();
		enableuploadButton();
	}
	
	
	
	@Override
	public void refreshinput(){
		resetCreate();
		resetUpload();
		resetSelectExisting();
	}
	
}
