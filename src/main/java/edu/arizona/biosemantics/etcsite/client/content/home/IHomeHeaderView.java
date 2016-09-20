package edu.arizona.biosemantics.etcsite.client.content.home;

import edu.arizona.biosemantics.etcsite.client.content.home.IHomeHeaderView.Presenter;

public interface IHomeHeaderView {

	public interface Presenter {

		void onAbout();
		
	}

	void setPresenter(Presenter presenter);
	
}
