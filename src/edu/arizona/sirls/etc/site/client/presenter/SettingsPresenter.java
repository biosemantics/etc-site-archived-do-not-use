package edu.arizona.sirls.etc.site.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.client.view.SettingsView;

public class SettingsPresenter implements Presenter, SettingsView.Presenter {

	private HandlerManager eventBus;
	private SettingsView view;

	public SettingsPresenter(HandlerManager eventBus, SettingsView view) {
		this.eventBus = eventBus;
		this.view = view;
		view.setPresenter(this);
		
		
	}

	@Override
	public void onSubmit() {
		//service do change; return if worked or not
		view.getOldPassword();
		view.getNewPassword();
		view.getConfirmedNewPassword();
		view.getBioportalUserId();
		view.getBioportalAPIKey();
	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}


}
