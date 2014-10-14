package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.rpc.user.IUserServiceAsync;

public class UsersPresenter implements IUsersView.Presenter {

	private IUsersView view;
	private IUserServiceAsync userService;

	@Inject
	public UsersPresenter(IUsersView view, IUserServiceAsync userService) {
		this.view = view;
		view.setPresenter(this);
		this.userService = userService;
	}
	
	@Override
	public void refresh() {
		userService.getUsers(Authentication.getInstance().getToken(), false, new AsyncCallback<List<ShortUser>>() {
			@Override
			public void onSuccess(List<ShortUser> result) {
				view.setUsers(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGetUsers(caught);
			}
		});
	}
	
	@Override
	public void setSelected(Set<ShortUser> selectedUsers) {
		view.setSelectedUsers(selectedUsers);
	}

	@Override
	public IUsersView getView() {
		return view;
	}

}
