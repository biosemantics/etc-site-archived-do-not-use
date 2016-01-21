package edu.arizona.biosemantics.etcsite.client.content.semanticMarkup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.oto2.oto.client.Oto;


public class SemanticMarkupReviewView extends Composite implements ISemanticMarkupReviewView, RequiresResize {

	private static SemanticMarkupReviewViewUiBinder uiBinder = GWT.create(SemanticMarkupReviewViewUiBinder.class);

	interface SemanticMarkupReviewViewUiBinder extends UiBinder<Widget, SemanticMarkupReviewView> {
	}

	private Presenter presenter;
	private Oto oto = new Oto();

	@UiField
	Button sendToOtoButton;
	
	@UiField
	SimpleLayoutPanel otoPanel;
	
	@Inject
	public SemanticMarkupReviewView() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		otoPanel.setWidget(oto.getView().asWidget());
		
		sendToOtoButton.setTitle("Contribute classifications to OTO");
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("sendToOtoButton")
	public void onSendToOto(ClickEvent event) {
		presenter.onSendToOto();
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}

	@Override
	public void onResize() {
		((RequiresResize)oto.getView()).onResize();
	}
	
	@Override
	public void setReview(int collectionId, String secret) {
		oto.setUser(Authentication.getInstance().getFirstName() + " " + 
			Authentication.getInstance().getLastName() + " (" + 
			Authentication.getInstance().getEmail() + ")");
		
		//don't want to initialize from history when coming back to the task again -> false
		oto.loadCollection(collectionId, secret, false);
	}
	
	@Override
	public Oto getOto() {
		return oto;
	}

	@Override
	public void setEnabledSendToOto(boolean value) {
		sendToOtoButton.setEnabled(value);
	}

}
