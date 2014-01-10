package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TitleCloseDialogBox;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;

public class UserSelectPresenter implements UserSelectView.Presenter {

	private IUserSelectView view;
	private TitleCloseDialogBox dialogBox;
	private ISelectListener currentListener;

	@Inject
	public UserSelectPresenter(IUserSelectView view) {
		this.view = view;
		view.setPresenter(this);
		this.dialogBox = new TitleCloseDialogBox("User Selection");
		this.dialogBox.setGlassEnabled(true);
		this.dialogBox.setWidget(view);
	}
	
	@Override
	public void show(ISelectListener listener) {
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
