package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.Set;

import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog;

import edu.arizona.biosemantics.etcsite.client.content.user.IUsersView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;

public class UserSelectPresenter implements IUserSelectView.Presenter {

	private IUserSelectView view;
	private Dialog dialog;
	private ISelectListener currentListener;
	private Presenter usersPresenter;

	@Inject
	public UserSelectPresenter(IUserSelectView view, IUsersView.Presenter usersPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.usersPresenter = usersPresenter;
		
		dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeadingText("Register");
		dialog.setPixelSize(-1, -1);
		dialog.setMinWidth(0);
		dialog.setMinHeight(0);
	    dialog.setResizable(true);
	    dialog.setShadow(true);
		dialog.setHideOnButtonClick(true);
		dialog.add(view.asWidget());
	}
	
	@Override
	public void show(ISelectListener listener) {
		usersPresenter.refresh();
		this.currentListener = listener;
		dialog.show();	
	}

	@Override
	public void onSelect(Set<ShortUser> users) {
		currentListener.onSelect(users);
		dialog.hide();
	}
	
	public interface ISelectListener {
		void onSelect(Set<ShortUser> user);
	}

}