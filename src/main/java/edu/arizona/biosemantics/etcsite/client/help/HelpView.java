package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

import edu.arizona.biosemantics.etcsitehelp.shared.help.HelpContent;

public class HelpView extends SimpleContainer implements IHelpView {

	private Presenter presenter;
	private ContentPanel panel = new ContentPanel();	
	private AccordionLayoutAppearance appearance;
	private AccordionLayoutContainer accordionLayoutContainer;
	private boolean hidden = true;
	
	public HelpView() {
		appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
		panel.setHeading("Help/Instructions");
		this.add(panel);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	

	public void setContent(JsArray<HelpContent> contents){
		accordionLayoutContainer = new AccordionLayoutContainer();
		accordionLayoutContainer.setExpandMode(ExpandMode.SINGLE); //ExpandMode.SINGLE_FILL does not layout correctly for some reason
		for(int i = 0; i < contents.length(); i++){
			HTML htmlContent = new HTML(contents.get(i).getContent());
			htmlContent.getElement().getStyle().setPadding(5, Unit.PX);
			ContentPanel subPanel = new ContentPanel(appearance);
			subPanel.setHeading(SafeHtmlUtils.fromString(contents.get(i).getTitle()));
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
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
		panel.setWidget(accordionLayoutContainer);
	}

	@Override
	public void onResize() {
		super.onResize();
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
