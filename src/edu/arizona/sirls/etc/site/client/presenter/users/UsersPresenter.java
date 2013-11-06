package edu.arizona.sirls.etc.site.client.presenter.users;

import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.arizona.sirls.etc.site.client.Authentication;
import edu.arizona.sirls.etc.site.client.presenter.Presenter;
import edu.arizona.sirls.etc.site.client.view.users.UsersView;
import edu.arizona.sirls.etc.site.shared.rpc.IUserServiceAsync;
import edu.arizona.sirls.etc.site.shared.rpc.RPCResult;
import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;

public class UsersPresenter implements Presenter, UsersView.Presenter {

	private HandlerManager eventBus;
	private UsersView view;
	private IUserServiceAsync userService;

	public UsersPresenter(HandlerManager eventBus, UsersView view, IUserServiceAsync userService) {
		this.eventBus = eventBus;
		this.view = view;
		this.userService = userService;
		view.setPresenter(this);
		
		this.refresh();
	}
	
	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
	}
	
	public void refresh() {
		userService.getUsers(Authentication.getInstance().getAuthenticationToken(), false, new AsyncCallback<RPCResult<List<ShortUser>>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

			}

			@Override
			public void onSuccess(RPCResult<List<ShortUser>> result) {
				if(result.isSucceeded())
					view.setUsers(result.getData());
			}
		});
	}

}
