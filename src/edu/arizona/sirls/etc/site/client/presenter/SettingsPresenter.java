package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class SettingsPresenter {

	public interface Display {
		Button getSubmitButton();
		Widget asWidget();
		HasText getOldPasswordTextBox();
		HasText getNewPasswordTextBox();
	}

	private Display display;
	private HandlerManager eventBus;

	public SettingsPresenter(HandlerManager eventBus, Display display) {
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		display.getSubmitButton().addClickHandler(
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						//service do change; return if worked or not
						display.getOldPasswordTextBox().getText();
						display.getNewPasswordTextBox().getText();
					}
				});
	}

	public void go(HasWidgets content) {
		content.clear();
		content.add(display.asWidget());
	}

}
