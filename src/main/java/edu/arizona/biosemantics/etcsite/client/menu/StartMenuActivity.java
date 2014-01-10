package edu.arizona.biosemantics.etcsite.client.menu;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.menu.IStartMenuView.Presenter;

public class StartMenuActivity extends AbstractActivity implements Presenter {

	private IStartMenuView startMenuView;

	@Inject
	public StartMenuActivity(IStartMenuView startMenuView) {
		this.startMenuView = startMenuView;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		startMenuView.setPresenter(this);
		panel.setWidget(startMenuView.asWidget());
	}

}
