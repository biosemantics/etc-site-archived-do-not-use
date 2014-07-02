package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.user.IUsersView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;

public class UserSelectPresenter implements IUserSelectView.Presenter {

	private IUserSelectView view;
	private PopupPanel dialogBox;
	private ISelectListener currentListener;
	private Presenter usersPresenter;

	@Inject
	public UserSelectPresenter(IUserSelectView view, IUsersView.Presenter usersPresenter) {
		this.view = view;
		view.setPresenter(this);
		this.usersPresenter = usersPresenter;
		this.dialogBox = new PopupPanel(true); //true means that the popup will close when the user clicks outside of it. 
		dialogBox.setGlassEnabled(true);
		dialogBox.add(view.asWidget());
	}
	
	@Override
	public void show(ISelectListener listener) {
		usersPresenter.refresh();
		this.currentListener = listener;
		dialogBox.center();	
	}

	@Override
	public void onSelect(Set<ShortUser> users) {
		currentListener.onSelect(users);
		dialogBox.hide();
	}
	
	public interface ISelectListener {
		void onSelect(Set<ShortUser> user);
	}

}