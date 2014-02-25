package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

public interface IEtcSiteView extends IsWidget {

	public interface Presenter {

		void onMouseOverHeader(MouseOverEvent event);

		void onMouseOutHeader(MouseOutEvent event);

		IEtcSiteView getView();

		void setHeaderSize(int size, boolean animated);


	}

	void setTop(IsWidget content);	
	void setMenu(IsWidget menu);
	void setContent(IsWidget content);
	void setPresenter(Presenter presenter);
	SimplePanel getContentContainer();
	SimplePanel getMenuContainer();
	SimplePanel getTopContainer();
	void setHeaderSize(int size, boolean animated);
	
}
