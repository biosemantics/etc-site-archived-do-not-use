package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

public class MessageConfirmCancelPresenter {

	public interface Display {
		Button getConfirmButton();
		Button getCancelButton();
		Widget asWidget();
		HTML getMessageHTML();
	}

	private TitleCloseDialogBox dialogBox;
	private Display display;
	
	public MessageConfirmCancelPresenter(Display display, String title, ClickHandler confirmHandler) { 
		this.dialogBox = new TitleCloseDialogBox(false, title); 
		this.display = display;
		bind(confirmHandler);
	}
	
	public void bind(final ClickHandler confirmHandler) { 
		display.getCancelButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		}); 
		display.getConfirmButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				confirmHandler.onClick(event);
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
