package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.Authentication;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;
import edu.arizona.biosemantics.etcsite.shared.rpc.IUserServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.RPCCallback;

public class UsersPresenter implements IUsersView.Presenter {

	private IUsersView view;
	private IUserServiceAsync userService;

	@Inject
	public UsersPresenter(IUsersView view, IUserServiceAsync userService) {
		this.view = view;
		view.setPresenter(this);
		this.userService = userService;
		this.refresh();
	}
	
	@Override
	public void refresh() {
		userService.getUsers(Authentication.getInstance().getToken(), false, new RPCCallback<List<ShortUser>>() {
			@Override
			public void onResult(List<ShortUser> result) {
				view.setUsers(result);
			}
		});
	}
	
	@Override
	public void setSelected(Set<ShortUser> selectedUsers) {
		view.setSelectedUsers(selectedUsers);
	}

}
