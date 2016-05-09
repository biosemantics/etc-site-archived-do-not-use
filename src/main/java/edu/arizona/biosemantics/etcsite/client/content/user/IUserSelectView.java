package edu.arizona.biosemantics.etcsite.client.content.user;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.etcsite.shared.model.ShortUser;

public interface IUserSelectView extends IsWidget {

	public interface Presenter {
		void show(ISelectListener listener, Set<ShortUser> selected);

		void hide();
	}
	
	public interface ISelectListener {
		void onSelect(Set<ShortUser> user);
	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	Set<ShortUser> getSelectedUsers();

}
