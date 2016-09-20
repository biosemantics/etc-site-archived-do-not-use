package edu.arizona.biosemantics.etcsite.client.content.news;

import com.google.gwt.user.client.ui.IsWidget;

public interface INewsView extends IsWidget {

	public interface Presenter {
		
	}

	void setPresenter(Presenter presenter);

	void setHtml(String html);
	
}
