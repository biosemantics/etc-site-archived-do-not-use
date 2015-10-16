package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;
import com.sencha.gxt.widget.core.client.Window;

public class HelpView extends Composite implements IHelpView {

	private static HelpHomeViewUiBinder uiBinder = GWT.create(HelpHomeViewUiBinder.class);

	interface HelpHomeViewUiBinder extends UiBinder<Widget, HelpView> {
	}

	private Presenter presenter;
	
	@UiField
	ContentPanel panel;
	
	@UiField
	Button openButton;
	
	@UiFactory
	public ContentPanel createContentPanel(ContentPanelAppearance appearance) {
	    return new ContentPanel(appearance);
	}
	
	public HelpView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("openButton")
	public void onOpenButton(ClickEvent e){
		Window newWindow = new Window();
		newWindow.setMaximizable(true);
		newWindow.setHeadingText("Help/Instructions");
		newWindow.setWidth("500");
		newWindow.setHeight("500");
		newWindow.add(panel.getWidget().asWidget());
		newWindow.show();
	}
	
	@Override
	public void setContent(IsWidget content) {
		panel.add(content);
	}

	
}
