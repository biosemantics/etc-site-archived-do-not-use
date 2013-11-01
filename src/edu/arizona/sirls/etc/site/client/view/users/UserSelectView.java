package edu.arizona.sirls.etc.site.client.view.users;

import java.util.Set;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.sirls.etc.site.shared.rpc.db.ShortUser;

public interface UserSelectView {

	public interface Presenter {
		void onSelect(Set<ShortUser> users);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();

}
