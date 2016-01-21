package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;

import edu.arizona.biosemantics.etcsite.etcsitehelp.shared.help.HelpContent;

public class HelpView extends Composite implements IHelpView {

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
		AccordionLayoutContainer newAccordion = new AccordionLayoutContainer();
		for(int i=0;i<contents.length();i++){
			HTML htmlContent = new HTML(contents.get(i).getContent());
			htmlContent.setStyleName(style.subPanel());
			ContentPanel subPanel = new ContentPanel(appearance);
			subPanel.setHeadingHtml(contents.get(i).getTitle());
			subPanel.add(htmlContent);
			
			if(newAccordion.getWidgetCount() == 0){
				newAccordion.add(subPanel);
			}else{
				newAccordion.insert(subPanel, newAccordion.getWidgetCount());
			}
		}
		panel.add(newAccordion);
	}
		
	
}
