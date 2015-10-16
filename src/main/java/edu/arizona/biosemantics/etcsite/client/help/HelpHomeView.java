package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class HelpHomeView extends Composite implements IHelpHomeView {

	private static HelpHomeViewUiBinder uiBinder = GWT.create(HelpHomeViewUiBinder.class);

	interface HelpHomeViewUiBinder extends UiBinder<Widget, HelpHomeView> {
	}
	
	
	public HelpHomeView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
}
