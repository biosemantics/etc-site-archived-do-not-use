package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;

public class HelpSemanticMarkupView extends Composite implements IHelpSemanticMarkupView {

	private static HelpHomeViewUiBinder uiBinder = GWT.create(HelpHomeViewUiBinder.class);

	interface HelpHomeViewUiBinder extends UiBinder<Widget, HelpSemanticMarkupView> {
	}
	
	
	public HelpSemanticMarkupView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiFactory
	public ContentPanel createContentPanel(ContentPanelAppearance appearance) {
	    return new ContentPanel(appearance);
	}
	
}
