package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class HelpPresenter implements Presenter {

	public interface Display {
		Widget asWidget();
	}

	private HandlerManager eventBus;
	private Display display;

	public HelpPresenter(HandlerManager eventBus, Display display) {
		this.eventBus = eventBus;
		this.display = display;
	}

	public void go(HasWidgets content) {
		content.clear();
		content.add(display.asWidget());
	}

}
