package edu.arizona.biosemantics.etcsite.client.content.news;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.news.INewsView;
import edu.arizona.biosemantics.etcsite.client.content.news.INewsView.Presenter;


public class NewsActivity extends MyAbstractActivity implements Presenter {

		private INewsView newsView;
		private PlaceController placeController;

		@Inject
		public NewsActivity(INewsView newsView, PlaceController placeController) {
			this.placeController = placeController;
			this.newsView = newsView;
		}
		
		@Override
		public void start(AcceptsOneWidget panel, EventBus eventBus) {
			newsView.setPresenter(this);
			panel.setWidget(newsView.asWidget());
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub
			
		}

	}



