package edu.arizona.sirls.etc.site.client.presenter.users;

import java.util.Set;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.users.UserSelectView;
import edu.arizona.sirls.etc.site.client.view.users.UsersView;
import edu.arizona.sirls.etc.site.shared.rpc.IUserServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;

public class UserSelectPresenter implements Presenter, UserSelectView.Presenter {

	private HandlerManager eventBus;
	private UserSelectView view;
	private ISelectHandler selectHandler;

	public UserSelectPresenter(HandlerManager eventBus, UserSelectView view, ISelectHandler selectHandler) {
		this.eventBus = eventBus;
		this.view = view;
		this.selectHandler = selectHandler;
		view.setPresenter(this);
	}
	
	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}

	@Override
	public void onSelect(Set<ShortUser> users) {
		selectHandler.onSelect(users);
	}
	
	public interface ISelectHandler {
		void onSelect(Set<ShortUser> user);
	}

}
