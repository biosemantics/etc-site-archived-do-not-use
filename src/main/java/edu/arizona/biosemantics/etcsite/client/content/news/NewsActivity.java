package edu.arizona.biosemantics.etcsite.client.content.news;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.etcsite.client.common.Alerter;
import edu.arizona.biosemantics.etcsite.client.common.ILoginView;
import edu.arizona.biosemantics.etcsite.client.common.IRegisterView;
import edu.arizona.biosemantics.etcsite.client.common.IResetPasswordView;
import edu.arizona.biosemantics.etcsite.client.content.news.INewsView.Presenter;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationServiceAsync;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.setup.ISetupServiceAsync;


public class NewsActivity extends MyAbstractActivity implements Presenter {
	
	private INewsView newsView;
	private ISetupServiceAsync setupService;
	
	@Inject
	public NewsActivity(INewsView newsView, PlaceController placeController, 
			IAuthenticationServiceAsync authenticationService, 
			ILoginView.Presenter loginPresenter, 
			IRegisterView.Presenter registerPresenter,
			IResetPasswordView.Presenter resetPasswordPresenter,
			ISetupServiceAsync setupService){
		super(placeController, authenticationService, loginPresenter, registerPresenter, resetPasswordPresenter);
		this.newsView = newsView;
		this.setupService = setupService;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		newsView.setPresenter(this);
		final MessageBox box = Alerter.startLoading();
		setupService.getNews(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.failedToGetNews(caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(String result) {
				Alerter.stopLoading(box);
				newsView.setHtml(result);
			}
		});
		panel.setWidget(newsView.asWidget());
	}
	
	@Override
	public void update() {
		
	}
}

