
package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.user.client.ui.IsWidget;

public interface IHomeView extends IsWidget {

	public interface Presenter extends IHomeHeaderView.Presenter, IHomeMainView.Presenter {
	}
	
	void setPresenter(Presenter presenter);

}
