package edu.arizona.biosemantics.etcsite.client.common;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
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
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import gwtupload.client.Uploader;

public class InputCreateView extends Composite implements IInputCreateView {

	private static SemanticmarkupCreateViewUiBinder uiBinder = GWT
			.create(SemanticmarkupCreateViewUiBinder.class);

	interface SemanticmarkupCreateViewUiBinder extends UiBinder<Widget, InputCreateView> {
	}

	private IInputCreateView.Presenter presenter;
	private ListStore<FolderTreeItem> ownedFoldersStore;
	private String inputlabel = "<html><font size=\"2\"><b>The input .csv file should follow these specifications:</b><br/><ol style=\"list-style:disc;padding: 5px 20px;\"><li>Each row/line represents a taxon. Each column represents a character.</li><li>Each column should be separated by comma(,).</li><li>Values for character should be separated using pipe (|). The values should be enclosed by double quote (\").</li> <li>Backslash(\\) can be used to escape characters.</li><li>Each taxon name should be in \"RANKNAME=taxon_name:author=authority_value,date=date_value\". Example: \"SPECIES=amabilis:author=etc,date=1900\".</li></ol></font></html>";
	@UiField Anchor sampleFileAnchor;
	
	@UiField VerticalPanel verticalPanel;
	
	@UiField RadioButton createFilesRadio;
	@UiField VerticalPanel createPanel;
	@UiField FlowPanel dummyCreatePanel1;
	@UiField FlowPanel dummyCreatePanel2;
	@UiField Anchor textCaptureAnchor1;
	@UiField Anchor textCaptureAnchor2;
	@UiField VerticalPanel dummyCreatePanel3;
	@UiField RadioButton createFolderForCreateFilesRadio;
	@UiField Button createFolderForCreateFilesButton;
	@UiField TextBox createFolderForCreateFilesTextBox;
	@UiField RadioButton selectFolderForCreateFilesRadio;
	@UiField(provided=true) ComboBox<FolderTreeItem> selectFolderForCreateFilesComboBox;
	@UiField Button createFilesButton;
	@UiField RadioButton dummyCreateFilesRadio1;
	@UiField RadioButton dummyCreateFilesRadio2;
	@UiField RadioButton dummyCreateFilesRadio3;
	@UiField RadioButton uploadRadio;
	@UiField VerticalPanel uploadPanel;
	@UiField RadioButton createFolderForUploadRadio;
	@UiField Button createFolderForUploadButton;
	@UiField TextBox createFolderForUploadTextBox;
	@UiField RadioButton selectFolderForUploadRadio;
	@UiField(provided=true) ComboBox<FolderTreeItem> selectFolderForUploadComboBox;
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
		ownedFoldersStore = new ListStore<FolderTreeItem>(new ModelKeyProvider<FolderTreeItem>() {
			@Override
			public String getKey(FolderTreeItem item) {
				return item.getName(false);
			}
	    });
	    LabelProvider<FolderTreeItem> nameLabelProvider = new LabelProvider<FolderTreeItem>() {
			@Override
			public String getLabel(FolderTreeItem item) {
				return item.getName(false);
			}
	    };
	    selectFolderForCreateFilesComboBox = new ComboBox<FolderTreeItem>(ownedFoldersStore, nameLabelProvider);
	    selectFolderForCreateFilesComboBox.setTriggerAction(TriggerAction.ALL);
		selectFolderForUploadComboBox = new ComboBox<FolderTreeItem>(ownedFoldersStore, nameLabelProvider);
		selectFolderForUploadComboBox.setTriggerAction(TriggerAction.ALL);
		selectFolderForCreateFilesComboBox.setEnabled(false);
		selectFolderForUploadComboBox.setEnabled(false);
		selectFolderForCreateFilesComboBox.addSelectionHandler(new SelectionHandler<FolderTreeItem>() {
            @Override
            public void onSelection (SelectionEvent<FolderTreeItem> event){
        			activiatecreateButton2();       
            }
		});	     		
		selectFolderForUploadComboBox.addSelectionHandler(new SelectionHandler<FolderTreeItem>() {
            @Override
            public void onSelection (SelectionEvent<FolderTreeItem> event){
        			activiateuploadButton2();       
            }
		});	     		
		initWidget(uiBinder.createAndBindUi(this));
		sampleFileAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
		createFolderForCreateFilesTextBox.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
		createFolderForUploadTextBox.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
		
		verticalPanel.remove(0);
		verticalPanel.remove(0);
	}

	@UiHandler("textCaptureAnchor2")
	public void onTextCapture2(ClickEvent event) {
		presenter.onTextCapture();
	}

	@UiHandler("textCaptureAnchor1")
	public void onTextCapture1(ClickEvent event) {
		presenter.onTextCapture();
	}
	
	@UiHandler("matrixGenerationAnchor")
	public void onMatrixGeneration(ClickEvent event) {
		presenter.onMatrixGeneration();
	}
	
	@UiHandler("sampleFileAnchor")
	public void onSample(ClickEvent event){
		MessageBox popUpMessageBox = new MessageBox("Key Generation Input Files  Format", inputlabel);
		popUpMessageBox.show();
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
	
	@UiHandler("dummyCreateFilesRadio3")
	public void onDummyCreateRadio3(ClickEvent event){
		createPanel.setVisible(false);
		dummyCreatePanel3.setVisible(true);
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
		createFolderForCreateFilesButton.setVisible(true);
		selectFolderForCreateFilesComboBox.setText(null);
		selectFolderForCreateFilesComboBox.setValue(null);
		disablecreateButton();
	}
	
	@UiHandler("selectFolderForCreateFilesRadio")
	public void onSelectFolderForCreateFilesRadio(ClickEvent event){
		createFolderForCreateFilesTextBox.setEnabled(false);
		selectFolderForCreateFilesComboBox.setEnabled(true);
		createFolderForCreateFilesButton.setVisible(false);
		disablecreateButton();
		createFolderForCreateFilesTextBox.setValue(null);
		presenter.deleteFolderForinputFiles();	
	}
	
	@UiHandler("createFolderForUploadRadio")
	public void onCreateFolderForUploadRadio(ClickEvent event){
		createFolderForUploadTextBox.setEnabled(true);
		selectFolderForUploadComboBox.setEnabled(false);
		createFolderForUploadButton.setVisible(true);
		selectFolderForUploadComboBox.setText(null);
		selectFolderForUploadComboBox.setValue(null);
		disableuploadButton();
	}
	
	@UiHandler("selectFolderForUploadRadio")
	public void onSelectFolderRadioUpload(ClickEvent event){
		createFolderForUploadTextBox.setEnabled(false);
		selectFolderForUploadComboBox.setEnabled(true);
		createFolderForUploadButton.setVisible(false);
		createFolderForUploadTextBox.setValue(null);
		presenter.deleteFolderForinputFiles();	
		disableuploadButton();
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
	public void activiateuploadButton1() {
		uploadButton.setEnabled(true);
		uploadButton.setText("Upload Files in New Folder");
	}
	
	private void activiateuploadButton2() {
		uploadButton.setEnabled(true);
		uploadButton.setText("Upload Files in Existing Folder");
	}
	
	@Override
	public void activiatecreateButton1() {
		createFilesButton.setEnabled(true);
		createFilesButton.setText("Create Files in New Folder");
	}
	
	private void activiatecreateButton2() {
		createFilesButton.setEnabled(true);
		createFilesButton.setText("Create Files in Existing Folder");
	}
	
	private void disableuploadButton() {
		uploadButton.setEnabled(false);
		uploadButton.setText("Upload Files");
	}
	
	private void disablecreateButton(){
		createFilesButton.setEnabled(false);
		createFilesButton.setText("Create Files");
	}
	
	
	
	@Override
	public void setOwnedFolders(List<FolderTreeItem> folders){
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
	public FolderTreeItem getSelectedFolderForCreateFiles() {
		return selectFolderForCreateFilesComboBox.getValue();
	}

	@Override
	public FolderTreeItem getSelectedFolderForUpload() {
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
	public void addDummyCreateFiles3() {
		verticalPanel.insert(dummyCreatePanel3, 0);
		verticalPanel.insert(dummyCreateFilesRadio3, 0);
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
		dummyCreateFilesRadio3.setChecked(false);
		dummyCreatePanel3.setVisible(false);
		presenter.deleteFolderForinputFiles();
		disableuploadButton();
		disablecreateButton();
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
		disableuploadButton();
		disablecreateButton();
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
		disableuploadButton();
		disablecreateButton();
	}
	
	
	
	@Override
	public void refreshinput(){
		resetCreate();
		resetUpload();
		resetSelectExisting();
	}
	
}
