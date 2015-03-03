package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HelpHomeView extends Composite implements IHelpHomeView {

	private static HelpHomeViewUiBinder uiBinder = GWT.create(HelpHomeViewUiBinder.class);

	interface HelpHomeViewUiBinder extends UiBinder<Widget, HelpHomeView> {
	}

	private Presenter presenter;
	
	
	public HelpHomeView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
}
