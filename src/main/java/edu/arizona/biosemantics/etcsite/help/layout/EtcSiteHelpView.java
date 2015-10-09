package edu.arizona.biosemantics.etcsite.help.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;

public class EtcSiteHelpView extends Composite implements IEtcSiteHelpView {

	private static EtcSiteHelpUiBinder uiBinder = GWT.create(EtcSiteHelpUiBinder.class);

	interface EtcSiteHelpUiBinder extends UiBinder<Widget, EtcSiteHelpView> {
	}
	
	
	@UiField
	VerticalPanel eastPanel;
	
	
	@UiField
	FocusPanel navigationPanel;
	
	@UiField
	SimpleLayoutPanel contentPanel;

	@UiField
	DockLayoutPanel dockLayoutPanel;
		
	private Presenter presenter;
	
	public EtcSiteHelpView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setContent(IsWidget content) {
		contentPanel.setWidget(content);
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public SimpleLayoutPanel getContentContainer() {
		return this.contentPanel;
	}
	
	
	
	/*@UiHandler("menu")
	void onMenuClick(ClickEvent e) {
		Double size = dockLayoutPanel.getWidgetSize(navigationPanel);
		if(size == 200) 
			setNavigationSize(0, true);
		else
			setNavigationSize(200, true);
	}*/
		
}