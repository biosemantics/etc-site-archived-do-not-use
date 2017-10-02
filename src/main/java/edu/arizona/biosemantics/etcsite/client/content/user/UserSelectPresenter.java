package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.Set;

import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.etcsite.client.content.user.IUserSelectView.ISelectListener;
import edu.arizona.biosemantics.etcsite.client.content.user.IUsersView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;

public class UserSelectPresenter implements IUserSelectView.Presenter {

	private IUserSelectView view;
	private Dialog dialog;
	private ISelectListener currentListener;
	private Presenter usersPresenter;

	@Inject
	public UserSelectPresenter(final IUserSelectView view, IUsersView.Presenter usersPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.usersPresenter = usersPresenter;
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("Select User");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.add(view.asWidget());
		dialog.getButton(PredefinedButton.OK).setText("Select");
		dialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				currentListener.onSelect(view.getSelectedUsers());
				dialog.hide();
			}
		});
	}
	
	@Override
	public void show(ISelectListener listener, Set<ShortUser> selected) {
		usersPresenter.refresh();
		this.currentListener = listener;
		dialog.show();	
	}

	@Override
	public void hide() {
		dialog.hide();
	}

}