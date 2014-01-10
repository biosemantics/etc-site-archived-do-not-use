package edu.arizona.biosemantics.etcsite.client.content.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HelpView extends Composite implements IHelpView {

	private static HelpViewUiBinder uiBinder = GWT.create(HelpViewUiBinder.class);

	interface HelpViewUiBinder extends UiBinder<Widget, HelpView> {
	}

	private Presenter presenter;

	public HelpView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	

}
