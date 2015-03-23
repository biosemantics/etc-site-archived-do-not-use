package edu.arizona.biosemantics.etcsite.client.content.home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HomeView extends Composite implements IHomeView {

	private static HomeViewUiBinder uiBinder = GWT.create(HomeViewUiBinder.class);

	interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
	}

	private Presenter presenter;

	@UiField
	IHomeHeaderView header;
	
	@UiField
	IHomeMainView main;
	
	public HomeView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		header.setPresenter(presenter);
		main.setPresenter(presenter);
	}
}
