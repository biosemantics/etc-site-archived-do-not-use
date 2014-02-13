package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class EtcSiteView extends Composite implements IEtcSiteView {

	private static EtcSiteUiBinder uiBinder = GWT.create(EtcSiteUiBinder.class);

	interface EtcSiteUiBinder extends UiBinder<Widget, EtcSiteView> {
	}
	
	@UiField
	SimplePanel topPanel;

	@UiField
	SimplePanel menuPanel;
	
	@UiField
	SimplePanel contentPanel;

	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel headerPanel;
	
	@UiField
	ScrollPanel contentScrollPanel;
	
	private Presenter presenter;
	
	public EtcSiteView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setTop(IsWidget content) {
		topPanel.setWidget(content);
	}
	
	@Override
	public void setContent(IsWidget content) {
		contentPanel.setWidget(content);
	}
	 
	@Override
	public void setMenu(IsWidget menu) {
		menuPanel.setWidget(menu);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public AcceptsOneWidget getContentContainer() {
		return this.contentPanel;
	}

	@Override
	public AcceptsOneWidget getMenuContainer() {
		return this.menuPanel;
	}

	@Override
	public AcceptsOneWidget getTopContainer() {
		return this.topPanel;
	}

	@Override
	public void setHeaderSize(int size) {
		dockLayoutPanel.setWidgetSize(headerPanel, size);
	}
	
}
