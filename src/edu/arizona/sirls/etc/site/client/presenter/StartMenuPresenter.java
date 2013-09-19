package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class StartMenuPresenter implements Presenter {

	private HandlerManager eventBus;
	public interface Display {
		Widget asWidget();
	}
	private final Display display;

	public StartMenuPresenter(HandlerManager eventBus, Display display) {
		this.eventBus = eventBus;
		this.display = display;
		bind();
	}

	private void bind() {
		
	}

	@Override
	public void go(HasWidgets content) {
		content.clear();
	    content.add(display.asWidget());
	}
}
