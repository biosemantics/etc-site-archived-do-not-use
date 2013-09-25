package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.MessageConfirmCancelView;

public class MessageResumeOrStartPresenter {

	public interface Display {
		Button getResumeButton();
		Button getStartButton();
		Widget asWidget();
		HTML getMessageHTML();
	}

	private TitleCloseDialogBox dialogBox;
	private Display display;
	
	public MessageResumeOrStartPresenter(Display display, String title, ClickHandler resumeHandler, ClickHandler startHandler) { 
		this.dialogBox = new TitleCloseDialogBox(false, title); 
		this.display = display;
		bind(resumeHandler, startHandler);
	}

	public void bind(final ClickHandler resumeHandler, final ClickHandler startHandler) { 
		display.getResumeButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				resumeHandler.onClick(event);
				dialogBox.hide();
			}
		}); 
		display.getStartButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startHandler.onClick(event);
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
