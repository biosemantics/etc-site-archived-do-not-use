package edu.arizona.biosemantics.etcsite.client.content.treeGeneration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.IInputCreateView;

public class TreeGenerationInputView extends Composite implements ITreeGenerationInputView{

	private static TreeGenerationCreateViewUiBinder uiBinder = GWT
			.create(TreeGenerationCreateViewUiBinder.class);

	interface TreeGenerationCreateViewUiBinder extends
			UiBinder<Widget, TreeGenerationInputView> {
	}

	private ITreeGenerationInputView.Presenter presenter;
	
	@UiField Anchor fileManagerAnchor;
	@UiField Anchor sampleFileAnchor;

	//@UiField DialogBox sampleFilePopup;
	
	@UiField(provided=true) 
	IInputCreateView inputCreateView;
	
	
	private String sampleMessageContent = "<textarea readonly cols=\"150\">\"Name\",\"coloration of apex\",\"prominence of apex\",\"shape of apex\",\"size of apex\"\n\"SPECIES=amabilis:author=etc.,date=1900\",\"\",\"exposed\",\"sharp-pointed | prominently notched | globose | rounded\",\"small\"\n\"SPECIES=balsamea:author=etc.,date=1900\",\"brown\",\"exposed\",\"acute | conic | slightly notched - rounded | sharp-pointed | round - obtuse\",\"small\"</textarea>";
	
	
	@Inject
	public TreeGenerationInputView(@Named("TreeGeneration")IInputCreateView.Presenter inputCreatePresenter) {		
		this.inputCreateView = inputCreatePresenter.getView();
	//	setUpDialog();
		initWidget(uiBinder.createAndBindUi(this));		
		fileManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
		sampleFileAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
	}
	/*
	private void setUpDialog() {
        samplePopup = new Modal();
    }*/
	
	@UiHandler("fileManagerAnchor")
	public void onFileManagerAnchor(ClickEvent event){
		presenter.onFileManager();
	}	

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public IInputCreateView getInputCreateView() {
		return inputCreateView;
	}
	
	@UiHandler("sampleFileAnchor")
	public void onSample(ClickEvent event){
		MessageBox popUpMessageBox = new MessageBox("Sample Input File", sampleMessageContent);
		popUpMessageBox.show();
	}
		
}
