package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

public class MatrixGenerationCreateView extends Composite implements IMatrixGenerationCreateView{

	private static MatrixGenerationCreateViewUiBinder uiBinder = GWT
			.create(MatrixGenerationCreateViewUiBinder.class);

	interface MatrixGenerationCreateViewUiBinder extends
			UiBinder<Widget, MatrixGenerationCreateView> {
	}

	private IMatrixGenerationCreateView.Presenter presenter;
	
	@UiField Anchor fileManagerAnchor;
	
	@UiField VerticalPanel uploadPanel;
	@UiField HorizontalPanel selectPanel;
	
	@UiField RadioButton uploadRadio;
	
	@UiField RadioButton newFolderRadio_upload;
	@UiField RadioButton selectFolderRadio_upload;
	@UiField TextBox newFolderTextBox_upload;
	@UiField(provided=true) ComboBox<FileInfo> selectFolderComboBox_upload;
	@UiField Button uploadFilesButton;
	@UiField Button createNewFolderButton_upload;
	@UiField Label createFolderStatusLabel_upload;
	@UiField Uploader uploader;
	@UiField SimplePanel statusWidgetContainer;
	
	@UiField Button selectFolderButton;
	@UiField Label selectedFolderLabel;
	
	@UiField Button nextButton;
	
	List<FileInfo> folderNames;
	
	ListStore<FileInfo> ownedFolderNameStore;
	
	public MatrixGenerationCreateView() {
		
		ownedFolderNameStore = new ListStore<FileInfo>(new ModelKeyProvider<FileInfo>() {
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
		selectFolderComboBox_upload = new ComboBox<FileInfo>(ownedFolderNameStore, nameLabelProvider);
		
		selectFolderComboBox_upload.setEnabled(false);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		newFolderTextBox_upload.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
		
		fileManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	
	@UiHandler("uploadRadio")
	public void onUploadRadio(ClickEvent event){
		uploadPanel.setVisible(true);
		selectPanel.setVisible(false);
		nextButton.setEnabled(true);
	}
	
	@UiHandler("selectRadio")
	public void onSelectRadio(ClickEvent event){
		uploadPanel.setVisible(false);
		selectPanel.setVisible(true);
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
	
	@UiHandler("createNewFolderButton_upload")
	public void onCreateNewFolderButton_upload(ClickEvent event){
		String folderName = newFolderTextBox_upload.getText();
		presenter.createNewFolder(folderName);
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
	@UiHandler("selectFolderButton")
	public void onselectFolder(ClickEvent event) {
		presenter.onSelect();
	}
	
	@UiHandler("fileManagerAnchor")
	public void onFileManagerAnchor(ClickEvent event){
		presenter.onFileManager();
	}
	
	public void setOwnedFolderNames(List<FileInfo> folders){
		this.folderNames = folders;
		ownedFolderNameStore.replaceAll(folders);
	}
	
	

	@Override
	public void setCreateFolderStatus(String status) {
			createFolderStatusLabel_upload.setText(status);
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
	
	public Boolean getNewFolderRadio_upload() {
		return newFolderRadio_upload.getValue();
	}

	public Boolean getSelectFolderRadio_upload() {
		return selectFolderRadio_upload.getValue();
	}

	public FileInfo getSelectFolderComboBox_upload() {
		return selectFolderComboBox_upload.getValue();
	}

	@Override
	public void setSelectedFolder(String shortendPath) {
		selectedFolderLabel.setText(shortendPath);
	}

	
}
