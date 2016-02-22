package edu.arizona.biosemantics.etcsite.client.content.taxonomyComparison;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
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

import edu.arizona.biosemantics.etcsite.shared.model.file.FolderTreeItem;
import gwtupload.client.Uploader;

public class InputCreateView extends Composite implements IInputCreateView {

	private static SemanticmarkupCreateViewUiBinder uiBinder = GWT
			.create(SemanticmarkupCreateViewUiBinder.class);

	interface SemanticmarkupCreateViewUiBinder extends UiBinder<Widget, InputCreateView> {
	}

	private IInputCreateView.Presenter presenter;
	private ListStore<FolderTreeItem> ownedFoldersStore;
	
	@UiField VerticalPanel verticalPanel;
		
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
	@UiField InlineLabel uploadedTaxonomiesLabel;
	
	@UiField RadioButton selectExistingFolderRadio;
	@UiField HorizontalPanel selectPanel1;	
	@UiField Button selectExistingFolderButton1;
	@UiField Label selectExistingFolderLabel1;
	@UiField HorizontalPanel selectPanel2;	
	@UiField Button selectExistingFolderButton2;
	@UiField Label selectExistingFolderLabel2;
	
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
		selectFolderForUploadComboBox = new ComboBox<FolderTreeItem>(ownedFoldersStore, nameLabelProvider);
		selectFolderForUploadComboBox.setTriggerAction(TriggerAction.ALL);
		selectFolderForUploadComboBox.setEnabled(false);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		createFolderForUploadTextBox.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
		uploadButton.setText("Upload Taxonomy Files into Folder");
	}

	@Override
	public void setPresenter(IInputCreateView.Presenter presenter) {
		this.presenter = presenter;
	}

	
	@UiHandler("uploadRadio")
	public void onUploadRadio(ClickEvent event){
		uploadPanel.setVisible(true);
		selectPanel1.setVisible(false);
		selectPanel2.setVisible(false);
	}
	
	@UiHandler("selectExistingFolderRadio")
	public void onSelectRadio(ClickEvent event){
		uploadPanel.setVisible(false);
		selectPanel1.setVisible(true);
		selectPanel2.setVisible(true);
		createFolderForUploadTextBox.setValue(null);
	}
	
	@UiHandler("createFolderForUploadRadio")
	public void onCreateFolderForUploadRadio(ClickEvent event){
		createFolderForUploadTextBox.setEnabled(true);
		selectFolderForUploadComboBox.setEnabled(false);
		uploadButton.setEnabled(true);
		createFolderForUploadButton.setVisible(true);
	}
	
	@UiHandler("selectFolderForUploadRadio")
	public void onSelectFolderRadioUpload(ClickEvent event){
		createFolderForUploadTextBox.setEnabled(false);
		selectFolderForUploadComboBox.setEnabled(true);
		createFolderForUploadButton.setVisible(false);
		uploadButton.setEnabled(true);
		createFolderForUploadTextBox.getElement().setPropertyString("placeholder", "Enter New Folder Name Here");
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
	
	@UiHandler("selectExistingFolderButton1")
	public void onSelectFolder1(ClickEvent event) {
		presenter.onSelectExistingFolder1();
	}
	
	@UiHandler("selectExistingFolderButton2")
	public void onSelectFolder2(ClickEvent event) {
		presenter.onSelectExistingFolder2();
	}
	
	@Override
	public void setOwnedFolders(List<FolderTreeItem> folders){
		ownedFoldersStore.replaceAll(folders);
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
	public boolean isCreateFolderForUpload() {
		return createFolderForUploadRadio.getValue();
	}
	
	@Override
	public boolean isSelectFolderForUpload() {
		return selectFolderForUploadRadio.getValue();
	}

	@Override
	public FolderTreeItem getSelectedFolderForUpload() {
		return selectFolderForUploadComboBox.getValue();
	}

	@Override
	public void setSelectedExistingFolder1(String shortendPath) {
		selectExistingFolderLabel1.setText(shortendPath);
	}
	
	@Override
	public void setSelectedExistingFolder2(String shortendPath) {
		selectExistingFolderLabel2.setText(shortendPath);
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
	public void setNextButtonName(String str) {
		nextButton.setText(str);
	}

	@Override
	public void setUploadedTaxonomies(String text) {
		uploadedTaxonomiesLabel.setText(text);
	}
	
}
