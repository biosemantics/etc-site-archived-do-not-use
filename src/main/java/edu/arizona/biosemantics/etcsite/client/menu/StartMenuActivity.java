package edu.arizona.biosemantics.etcsite.client.menu;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.about.AboutPlace;
import edu.arizona.biosemantics.etcsite.client.menu.IStartMenuView.Presenter;

public class StartMenuActivity extends AbstractActivity implements Presenter {

	private IStartMenuView startMenuView;
	private PlaceController placeController;

	@Inject
	public StartMenuActivity(IStartMenuView startMenuView, PlaceController placeController) {
		this.startMenuView = startMenuView;
		this.placeController = placeController;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		startMenuView.setPresenter(this);
		panel.setWidget(startMenuView.asWidget());
	}

	@Override
	public void onAbout() {
		placeController.goTo(new AboutPlace());
	}

}
