package edu.arizona.biosemantics.etcsite.client.content.sample;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface ISampleView extends IsWidget {

	public interface Presenter {

	}
	  
	void setPresenter(Presenter presenter);
	Widget asWidget();
	
}
