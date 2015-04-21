package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import java.util.List;

import net.sf.saxon.expr.instruct.NextMatch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import edu.arizona.biosemantics.etcsite.shared.model.file.FileInfo;

public class SemanticMarkupCreateView extends Composite implements ISemanticMarkupCreateView{

	private static SemanticmarkupCreateViewUiBinder uiBinder = GWT
			.create(SemanticmarkupCreateViewUiBinder.class);

	interface SemanticmarkupCreateViewUiBinder extends
			UiBinder<Widget, SemanticMarkupCreateView> {
	}

	private ISemanticMarkupCreateView.Presenter presenter;
	
	@UiField VerticalPanel createPanel;
	@UiField VerticalPanel uploadPanel;
	@UiField VerticalPanel selectPanel;
	
	@UiField RadioButton createRadio;
	@UiField RadioButton uploadRadio;
	@UiField RadioButton selectRadio;
	
	@UiField RadioButton newFolderRadio_create;
	@UiField RadioButton selectFolderRadio_create;
	@UiField TextBox newFolderTextBox_create;
	@UiField(provided=true) ComboBox<FileInfo> selectFolderComboBox_create;
	@UiField Button createFilesButton;
	
	@UiField RadioButton newFolderRadio_upload;
	@UiField RadioButton selectFolderRadio_upload;
	@UiField TextBox newFolderTextBox_upload;
	@UiField(provided=true) ComboBox<FileInfo> selectFolderComboBox_upload;
	@UiField Button uploadFilesButton;
	
	@UiField(provided=true) ComboBox<FileInfo> selectFolderComboBox_select;
	
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
		selectFolderComboBox_select = new ComboBox<FileInfo>(store, labelProvider);
		
		selectFolderComboBox_create.setEnabled(false);
		selectFolderComboBox_upload.setEnabled(false);
		selectFolderComboBox_select.setEnabled(false);
		
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("createRadio")
	public void onCreateRadio(ClickEvent event){
		createPanel.setVisible(true);
		uploadPanel.setVisible(false);
		selectPanel.setVisible(false);
		
	}
	
	@UiHandler("uploadRadio")
	public void onUploadRadio(ClickEvent event){
		createPanel.setVisible(false);
		uploadPanel.setVisible(true);
		selectPanel.setVisible(false);
	}
	
	@UiHandler("selectRadio")
	public void onSelectRadio(ClickEvent event){
		createPanel.setVisible(false);
		uploadPanel.setVisible(false);
		selectPanel.setVisible(true	);
	}
	
	@UiHandler("newFolderRadio_create")
	public void onNewFolderRadioCreate(ClickEvent event){
		newFolderTextBox_create.setEnabled(true);
		selectFolderComboBox_create.setEnabled(false);
		createFilesButton.setEnabled(true);
	}
	
	@UiHandler("selectFolderRadio_create")
	public void onSelectFolderRadioCreate(ClickEvent event){
		newFolderTextBox_create.setEnabled(false);
		selectFolderComboBox_create.setEnabled(true);
		createFilesButton.setEnabled(true);
	}
	
	@UiHandler("newFolderRadio_upload")
	public void onNewFolderRadioUpload(ClickEvent event){
		newFolderTextBox_upload.setEnabled(true);
		selectFolderComboBox_upload.setEnabled(false);
		uploadFilesButton.setEnabled(true);
	}
	
	@UiHandler("selectFolderRadio_upload")
	public void onSelectFolderRadioUpload(ClickEvent event){
		newFolderTextBox_upload.setEnabled(false);
		selectFolderComboBox_upload.setEnabled(true);
		uploadFilesButton.setEnabled(true);
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}
	
	public void setFolderNames(List<FileInfo> folderNames){
		this.folderNames = folderNames;
		store.replaceAll(folderNames);
	}
}
