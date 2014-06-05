package edu.arizona.biosemantics.etcsite.client.content.news;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IMessageOkView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.content.news.INewsView;
import edu.arizona.biosemantics.etcsite.client.content.news.INewsView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.IAuthenticationServiceAsync;


public class NewsActivity extends MyAbstractActivity implements Presenter {

		private INewsView newsView;

		@Inject
		public NewsActivity(INewsView newsView, PlaceController placeController, 
				IAuthenticationServiceAsync authenticationService, 
				ILoginView.Presenter loginPresenter, 
				IRegisterView.Presenter registerPresenter,
				IResetPasswordView.Presenter resetPasswordPresenter, 
				IMessageOkView.Presenter messagePresenter){
			super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter, messagePresenter);
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



