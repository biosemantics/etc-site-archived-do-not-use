package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.help.IHelpView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.help.Help;
import edu.arizona.biosemantics.etcsite.shared.help.Help.Type;

public class HelpHomeActivity extends AbstractActivity implements Presenter {

	private PlaceController placeController;
	private IHelpView helpView;

	@Inject
	public HelpHomeActivity(final IHelpView helpView, PlaceController placeController) {
		this.helpView = helpView;
		this.placeController = placeController;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
		helpView.setPresenter(this);
		helpView.setContent(Help.getHelp(Type.HOME));
		panel.setWidget(helpView.asWidget());
	}
}