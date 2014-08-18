package edu.arizona.biosemantics.etcsite.client.content.help;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.content.help.IHelpView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;

public class HelpActivity extends MyAbstractActivity implements Presenter {

	private IHelpView helpView;

	@Inject
	public HelpActivity(IHelpView helpView, 
			PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter,
			IResetPasswordView.Presenter resetPasswordPresenter) {
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
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

