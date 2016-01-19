package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

import edu.arizona.biosemantics.etcsitehelp.shared.help.HelpContent;

public class HelpView extends Composite implements IHelpView, RequiresResize {

	private static HelpHomeViewUiBinder uiBinder = GWT.create(HelpHomeViewUiBinder.class);

	interface HelpHomeViewUiBinder extends UiBinder<Widget, HelpView> {
	}
	
	interface HelpStyle extends CssResource{
		String subPanel();
	}

	private Presenter presenter;
	
	@UiField
	ContentPanel panel;
	
	@UiField HelpStyle style;
	
	AccordionLayoutAppearance appearance;

	private AccordionLayoutContainer accordionLayoutContainer;

	private boolean hidden = true;
	
	@UiFactory
	public ContentPanel createContentPanel(ContentPanelAppearance appearance) {
	    return new ContentPanel(appearance);
	}
	
	public HelpView() {
		appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	

	public void addContent(JsArray<HelpContent> contents){
		accordionLayoutContainer = new AccordionLayoutContainer();
		for(int i=0;i<contents.length();i++){
			HTML htmlContent = new HTML(contents.get(i).getContent());
			htmlContent.setStyleName(style.subPanel());
			ContentPanel subPanel = new ContentPanel(appearance);
			subPanel.setHeadingHtml(contents.get(i).getTitle());
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			flowLayoutContainer.setScrollMode(ScrollMode.AUTO);
			flowLayoutContainer.add(htmlContent);
			subPanel.add(flowLayoutContainer);
			
			if(accordionLayoutContainer.getWidgetCount() == 0) {
				accordionLayoutContainer.add(subPanel);
			} else {
				accordionLayoutContainer.insert(subPanel, accordionLayoutContainer.getWidgetCount());
			}
		}
		if(!hidden)
			accordionLayoutContainer.setActiveWidget(accordionLayoutContainer.getWidget(0));
		panel.add(accordionLayoutContainer);
	}

	@Override
	public void onResize() {
		//accordionLayoutContainer.setActiveWidget(null);
		//accordionLayoutContainer.setActiveWidget(accordionLayoutContainer.getWidget(0));
	}

	@Override
	public void onShow() {
		this.hidden = false;
		expandDefaultItem();
	}
	
	@Override
	public void onHide() {
		this.hidden = true;
	}

	private void expandDefaultItem() {
		Widget activeWidget = accordionLayoutContainer.getActiveWidget();
		if(activeWidget == null)
			accordionLayoutContainer.setActiveWidget(accordionLayoutContainer.getWidget(0));
	}
		
	
}
