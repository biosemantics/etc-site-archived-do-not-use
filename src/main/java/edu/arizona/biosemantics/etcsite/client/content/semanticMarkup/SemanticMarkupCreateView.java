package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;
import gwtupload.client.Uploader;

public class SemanticMarkupCreateView extends Composite implements ISemanticMarkupCreateView{

	private static SemanticmarkupCreateViewUiBinder uiBinder = GWT
			.create(SemanticmarkupCreateViewUiBinder.class);

	interface SemanticmarkupCreateViewUiBinder extends
			UiBinder<Widget, SemanticMarkupCreateView> {
	}

	private ISemanticMarkupCreateView.Presenter presenter;
	
	@UiField VerticalPanel createPanel;
	@UiField VerticalPanel uploadPanel;
	
	@UiField RadioButton createRadio;
	@UiField RadioButton uploadRadio;
	
	@UiField RadioButton newFolderRadio_create;
	@UiField RadioButton selectFolderRadio_create;
	@UiField TextBox newFolderTextBox_create;
	@UiField(provided=true) ComboBox<FileInfo> selectFolderComboBox_create;
	@UiField Button createFilesButton;
	@UiField Button createNewFolderButton_create;
	@UiField Label createFolderStatusLabel_create;
	
	@UiField RadioButton newFolderRadio_upload;
	@UiField RadioButton selectFolderRadio_upload;
	@UiField TextBox newFolderTextBox_upload;
	@UiField(provided=true) ComboBox<FileInfo> selectFolderComboBox_upload;
	@UiField Button uploadFilesButton;
	@UiField Button createNewFolderButton_upload;
	@UiField Label createFolderStatusLabel_upload;
	@UiField Uploader uploader;
	@UiField SimplePanel statusWidgetContainer;
	
	@UiField Button nextButton;
	
	List<FileInfo> folderNames;
	
	ListStore<FileInfo> store;
	
	public SemanticMarkupCreateView() {
		
		store = new ListStore<FileInfo>(new ModelKeyProvider<FileInfo>() {
			@Override
			public String getKey(FileInfo item) {
				return item.getName(false);
			}
	    });		
	    LabelProvider<FileInfo> labelProvider = new LabelProvider<FileInfo>() {
			@Override
			public String getLabel(FileInfo item) {
				return item.getName(false);
			}
	    };
		selectFolderComboBox_create = new ComboBox<FileInfo>(store, labelProvider);
		selectFolderComboBox_upload = new ComboBox<FileInfo>(store, labelProvider);
		
		selectFolderComboBox_create.setEnabled(false);
		selectFolderComboBox_upload.setEnabled(false);
		
		
		
		
		initWidget(uiBinder.createAndBindUi(this));
		
		newFolderTextBox_upload.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
		newFolderTextBox_create.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("createRadio")
	public void onCreateRadio(ClickEvent event){
		createPanel.setVisible(true);
		uploadPanel.setVisible(false);
		
	}
	
	@UiHandler("uploadRadio")
	public void onUploadRadio(ClickEvent event){
		createPanel.setVisible(false);
		uploadPanel.setVisible(true);
		
	}
	
	@UiHandler("newFolderRadio_create")
	public void onNewFolderRadioCreate(ClickEvent event){
		newFolderTextBox_create.setEnabled(true);
		selectFolderComboBox_create.setEnabled(false);
		createFilesButton.setEnabled(true);
		createNewFolderButton_create.setVisible(true);
		createFilesButton.setText("Create Files in New Folder");
		
	}
	
	@UiHandler("selectFolderRadio_create")
	public void onSelectFolderRadioCreate(ClickEvent event){
		newFolderTextBox_create.setEnabled(false);
		selectFolderComboBox_create.setEnabled(true);
		createNewFolderButton_create.setVisible(false);
		createFilesButton.setEnabled(true);
		createFilesButton.setText("Create Files in Selected Folder");
	}
	
	@UiHandler("newFolderRadio_upload")
	public void onNewFolderRadioUpload(ClickEvent event){
		newFolderTextBox_upload.setEnabled(true);
		selectFolderComboBox_upload.setEnabled(false);
		uploadFilesButton.setEnabled(true);
		createNewFolderButton_upload.setVisible(true);
		uploadFilesButton.setText("Upload Files in New Folder");
	}
	
	@UiHandler("selectFolderRadio_upload")
	public void onSelectFolderRadioUpload(ClickEvent event){
		newFolderTextBox_upload.setEnabled(false);
		selectFolderComboBox_upload.setEnabled(true);
		createNewFolderButton_upload.setVisible(false);
		uploadFilesButton.setEnabled(true);
		uploadFilesButton.setText("Upload Files in Selected Folder");
	}
	
	@UiHandler("createFilesButton")
	public void onCreateFiles(ClickEvent event){
		if(newFolderRadio_create.getValue()){
				presenter.createFilesInNewFolder();
		}else{
			presenter.createFiles(selectFolderComboBox_create.getValue());
		}
	}
	
	/*@UiHandler("uploadFilesButton")
	public void onUploadFiles(ClickEvent event){
		if(newFolderRadio_upload.getValue()){
			String folderName = newFolderTextBox_upload.getText();
			boolean isFolderCreated = presenter.createNewFolder(folderName);
			if(isFolderCreated){
				presenter.createFilesInNewFolder();
			}
		}else{
			presenter.createFiles(selectFolderComboBox_create.getValue());
		}
		
	}*/
	
	@UiHandler("createNewFolderButton_create")
	public void onCreateNewFolderButton_create(ClickEvent event){
		String folderName = newFolderTextBox_create.getText();
		presenter.createNewFolder(folderName);
	}
	
	@UiHandler("createNewFolderButton_upload")
	public void onCreateNewFolderButton_upload(ClickEvent event){
		String folderName = newFolderTextBox_upload.getText();
		presenter.createNewFolder(folderName);
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
	public void setFolderNames(List<FileInfo> folderNames){
		this.folderNames = folderNames;
		store.replaceAll(folderNames);
	}

	@Override
	public void setCreateFolderStatus(String status) {
		if(createRadio.getValue()){
			createFolderStatusLabel_create.setText(status);
		}else{
			createFolderStatusLabel_upload.setText(status);
		}
	}
	
	public boolean getCreateRadioValue() {
		return createRadio.getValue();
	}

	public boolean getUploadRadioValue() {
		return uploadRadio.getValue();
	}

	public Uploader getUploader() {
		return uploader;
	}

	@Override
	public Button getUploadButton() {
		return uploadFilesButton;
	}

	@Override
	public String getSelectedUploadDirectory() {
		return selectFolderComboBox_upload.getValue().getFilePath();
	}

	@Override
	public void setStatusWidget(Widget widget) {
		statusWidgetContainer.setWidget(widget);
	}

	@Override
	public void enableNextButton(boolean value) {
		nextButton.setEnabled(value);
		
	}

	public boolean getNewFolderRadio_create() {
		return newFolderRadio_create.getValue();
	}

	public Boolean getSelectFolderRadio_create() {
		return selectFolderRadio_create.getValue();
	}

	public FileInfo getSelectFolderComboBox_create() {
		return selectFolderComboBox_create.getValue();
	}

	public Boolean getNewFolderRadio_upload() {
		return newFolderRadio_upload.getValue();
	}

	public Boolean getSelectFolderRadio_upload() {
		return selectFolderRadio_upload.getValue();
	}

	public FileInfo getSelectFolderComboBox_upload() {
		return selectFolderComboBox_upload.getValue();
	}
	
	
}
