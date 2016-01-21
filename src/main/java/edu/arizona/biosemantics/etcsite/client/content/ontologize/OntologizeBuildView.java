package edu.arizona.biosemantics.etcsite.client.content.ontologize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.core.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.client.content.ontologize.IOntologizeBuildView.Presenter;
import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.oto.client.Oto;

public class OntologizeBuildView extends Composite implements IOntologizeBuildView, RequiresResize {

	private static OntologyBuildUiBinder uiBinder = GWT
			.create(OntologyBuildUiBinder.class);

	interface OntologyBuildUiBinder extends
			UiBinder<Widget, OntologizeBuildView> {
	}
	
	@UiField
	SimpleLayoutPanel ontologizePanel;
	
	private Presenter presenter;
	private Ontologize ontologize = new Ontologize();

	public OntologizeBuildView() {
		initWidget(uiBinder.createAndBindUi(this));
		ontologizePanel.setWidget(ontologize.getView().asWidget());
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("addFilesButton")
	public void onAddInput(ClickEvent event) {
		presenter.onAddInput();
	}
	
	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		presenter.onNext();
	}

	@Override
	public Ontologize getOntologize() {
		return ontologize;
	}

	@Override
	public void setOntologize(int uploadId, String secret) {
		ontologize.setUser(Authentication.getInstance().getFirstName() + " " + 
				Authentication.getInstance().getLastName() + " (" + 
				Authentication.getInstance().getEmail() + ")");
		
		ontologize.loadCollection(uploadId, secret);
	}

	@Override
	public void onResize() {
		((RequiresResize)ontologize.getView()).onResize();
	}
	
}
