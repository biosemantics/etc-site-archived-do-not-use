package edu.arizona.biosemantics.etcsite.client.content.news;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class NewsView extends Composite implements INewsView {

	private static NewsViewUiBinder uiBinder = GWT.create(NewsViewUiBinder.class);

	interface NewsViewUiBinder extends UiBinder<Widget, NewsView> {
	}

	private Presenter presenter;

	public NewsView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	

}

