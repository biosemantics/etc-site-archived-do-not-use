package edu.arizona.biosemantics.etcsite.client.content.help;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.help.IHelpView.Presenter;

public class HelpActivity extends MyAbstractActivity implements Presenter {

	private IHelpView helpView;
	private PlaceController placeController;

	@Inject
	public HelpActivity(IHelpView helpView, PlaceController placeController) {
		this.placeController = placeController;
		this.helpView = helpView;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		helpView.setPresenter(this);
		panel.setWidget(helpView.asWidget());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
