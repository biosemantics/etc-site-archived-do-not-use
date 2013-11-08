package edu.arizona.sirls.etc.site.client.view.users;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;

public interface UsersView {

	public interface Presenter {
		
	}
	  
	void setUsers(List<ShortUser> users);
	void setPresenter(Presenter presenter);
	Widget asWidget();
	Set<ShortUser> getSelectedUsers();
	void setSelectedUsers(Set<ShortUser> selectedUsers);

}
