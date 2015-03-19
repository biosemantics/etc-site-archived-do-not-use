package edu.arizona.biosemantics.etcsite.client.help;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.shared.help.Help;
import edu.arizona.biosemantics.etcsite.shared.help.Help.Type;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.client.help.IHelpView.Presenter;

public class HelpSemanticMarkupActivity extends MyAbstractActivity implements Presenter {
	
	private IHelpView view;

	@Inject
	public HelpSemanticMarkupActivity(IHelpView view, 
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter, 
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.view = view;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		view.setPresenter(this);
		view.setContent(Help.getHelp(Type.SemanticMarkupInput));
		panel.setWidget(view.asWidget());
	}

	@Override
	public void update() {
		
	}
}
