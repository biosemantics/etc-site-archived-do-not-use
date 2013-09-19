package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

public class MessagePresenter {

	public interface Display {
		Button getCloseButton();
		Widget asWidget();
		HTML getMessageHTML();
	}

	private TitleCloseDialogBox dialogBox;
	private Display display;
	
	public MessagePresenter(Display display, String title) { 
		this.dialogBox = new TitleCloseDialogBox(false, title); 
		this.display = display;
		bind();
	}
	
	public void bind() { 
		display.getCloseButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		}); 
	}
	
	public void setMessage(String message) {
		display.getMessageHTML().setHTML(message);
	}
	
	public void go() {
		dialogBox.setGlassEnabled(true);
		dialogBox.clear();
		dialogBox.add(display.asWidget());
		dialogBox.center();
	}
	
}
