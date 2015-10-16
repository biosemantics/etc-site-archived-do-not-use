package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.help.IHelpView.Presenter;

public class HelpHomeActivity extends AbstractActivity implements Presenter {

	private PlaceController placeController;
	private IHelpView helpView;
	private IHelpHomeView homeView;

	@Inject
	public HelpHomeActivity(final IHelpView helpView, IHelpHomeView homeView, PlaceController placeController) {
		this.helpView = helpView;
		this.homeView = homeView;
		this.placeController = placeController;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
		helpView.setPresenter(this);
		helpView.setContent(homeView.asWidget());
		panel.setWidget(helpView.asWidget());
	}
}