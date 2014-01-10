package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.client.content.user.UserSelectPresenter.ISelectListener;
import edu.arizona.biosemantics.etcsite.shared.db.ShortUser;

public interface IUserSelectView extends IsWidget {

	public interface Presenter {
		void onSelect(Set<ShortUser> users);
		void show(ISelectListener listener);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();

}
