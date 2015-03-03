package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HelpSemanticMarkupView extends Composite implements IHelpSemanticMarkupView {

	private static HelpSemanticMarkupViewUiBinder uiBinder = GWT.create(HelpSemanticMarkupViewUiBinder.class);

	interface HelpSemanticMarkupViewUiBinder extends UiBinder<Widget, HelpSemanticMarkupView> {
	}

	private Presenter presenter;
	
	
	public HelpSemanticMarkupView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
}
