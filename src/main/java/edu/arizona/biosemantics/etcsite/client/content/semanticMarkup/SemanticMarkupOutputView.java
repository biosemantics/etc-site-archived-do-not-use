package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.box.MessageBox;

public class SemanticMarkupOutputView extends Composite implements ISemanticMarkupOutputView {

	private static SemanticMarkupOutputViewUiBinder uiBinder = GWT
			.create(SemanticMarkupOutputViewUiBinder.class);

	interface SemanticMarkupOutputViewUiBinder extends
			UiBinder<Widget, SemanticMarkupOutputView> {
	}
	
	@UiField 
	HTMLPanel htmlpanel;   
    
	@UiField
	Anchor whatIsOto;
	
	@UiField
	Anchor fileManagerAnchor;
	
	@UiField 
	Anchor matrixGenerationAnchor;
	
	//@UiField 
	//Anchor myAccount;
	
	@UiField
	FlowPanel sendToOtoPanel;
	
	@UiField 
	Button sendToOto;
	
	@UiField
	InlineLabel outputLabel;
	
	@UiField
	InlineLabel outputLabelTermReview;

	private Presenter presenter;

	private String outputFull;
	
	int k=1;
		
	public SemanticMarkupOutputView() {
		initWidget(uiBinder.createAndBindUi(this));
		fileManagerAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
		matrixGenerationAnchor.getElement().getStyle().setCursor(Cursor.POINTER);
		sendToOto.getElement().getStyle().setCursor(Cursor.POINTER);
		whatIsOto.getElement().getStyle().setCursor(Cursor.POINTER);
		sendToOto.getElement().getStyle().setCursor(Cursor.POINTER);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("fileManagerAnchor") 
	public void onFileManager(ClickEvent event) {
		presenter.onFileManager();
	}
	
	@UiHandler("matrixGenerationAnchor") 
	public void onMatrixGeneration(ClickEvent event) {
		presenter.onContinueMatrixGeneration(this.outputFull);
	}
	
	@UiHandler("whatIsOto") 
	public void onWhatIsOTO (ClickEvent event) {
		if (this.k==1){
			htmlpanel.setVisible(true);
			this.k=2;
		}
		else {
			htmlpanel.setVisible(false);
			this.k=1;
		}
	}


	@Override
	public void setOutput(String outputFull, String outputFullDisplay, String outputTermReview) {
		this.outputFull = outputFull;
		this.outputLabel.setText(outputFullDisplay);
		outputTermReview=outputFullDisplay.replaceAll("_output_", "_TermsReviewed_");
		this.outputLabelTermReview.setText(outputTermReview);
	}

	@UiHandler("sendToOto")
	public void onSendToOto(ClickEvent event) {
		//if(sendToOto.isEnabled()){
			//sendToOto.setTitle("");
			presenter.onSendToOto();
		//}
		//else {
			//sendToOto.setTitle("This button is not enabled, please follow the instruction below to activate it. ");
		//}
			
		
	}
	
	/*@UiHandler("myAccount")
	public void onMyAccount(ClickEvent event) {
		presenter.onMyAccount();
	}*/

	@Override
	public void setEnabledSendToOto(boolean value) {
		sendToOtoPanel.setVisible(value);
		//sendToOto.setEnabled(value);
	}
	
	/*@Override
	public void setTitleSendToOto(boolean value) {
		if(value!=true){
			sendToOto.setTitle("This button is not enabled, please follow the instruction below to activate it. ");
		}
		else 
			sendToOto.setTitle("");
		
	}*/
	
}
