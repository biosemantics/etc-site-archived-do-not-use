package edu.arizona.biosemantics.etcsite.client.common;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;

public class MessageOkPresenter implements IMessageOkView.Presenter {

	private IMessageOkView view;
	private PopupPanel dialogBox;

	@Inject
	public MessageOkPresenter(IMessageOkView view, IAuthenticationServiceAsync authenticationService) {
		this.view = view;
		view.setPresenter(this);
		this.dialogBox = new PopupPanel(true); //true means that the popup will close when the user clicks outside of it. 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
	}

	@Override
	public void onOk() {
		dialogBox.hide();
	}
	
	@Override 
	public void setMessage(String message){
		view.setMessage(message);
	}
	
	@Override
	public void show() {
		dialogBox.center();
	}
}
