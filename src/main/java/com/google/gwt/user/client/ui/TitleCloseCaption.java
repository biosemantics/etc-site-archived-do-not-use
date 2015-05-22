package com.google.gwt.user.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.TitleCloseDialogBox.Caption;

public class TitleCloseCaption extends FocusPanel implements Caption {

	/*
	 * That�s what we have done here, and it works very well. 
	 * We�ve made our own Caption class that extends FocusablePanel 
	 * (a SimplePanel that captures all mouse events) and we added a 
	 * HorizontalPanel to it, with buttons and text. We had to override onAttach() 
	 * and onDetach() just by calling the super method (they are protected).
	 * 
	 */
	
	private HTML html;
	private TitleCloseDialogBox myDialogBox;
	
	public TitleCloseCaption(String title, TitleCloseDialogBox myDialogBox) {
		setStyleName("Caption");
		this.myDialogBox = myDialogBox;
		
		html = new HTML(title);
		html.addStyleName("titleText");
		Image image = new Image("images/revoke.jpg");
		image.addStyleName("clickable");
		image.setSize("15px", "15px");
		image.addStyleName("closingButton");
		image.getElement().getStyle().setProperty("float", "right");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.getElement().getStyle().setProperty("width", "100%");
		horizontalPanel.addStyleName("dialogBoxTitle");
		horizontalPanel.add(html);
		horizontalPanel.add(image);
		this.add(horizontalPanel);
		
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TitleCloseCaption.this.myDialogBox.hide();
			}
		});
	}

	@Override
	public String getText() {
		return html.getText();
	}

	@Override
	public void setText(String text) {
		html.setText(text);
	}

	@Override
	public String getHTML() {
		return this.html.getHTML();
	}

	@Override
	public void setHTML(String html) {
		this.html.setHTML(html);
	}

	@Override
	public void setHTML(SafeHtml html) {
		this.html.setHTML(html);
	}
}
