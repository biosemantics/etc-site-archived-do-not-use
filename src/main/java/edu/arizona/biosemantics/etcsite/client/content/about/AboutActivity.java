package edu.arizona.biosemantics.etcsite.client.content.about;

import com.google.gwt.activity.shared.MyAbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.content.about.IAboutView;
import edu.arizona.biosemantics.etcsite.client.content.about.IAboutView.Presenter;


public class AboutActivity extends MyAbstractActivity implements Presenter {

		private IAboutView aboutView;
		private PlaceController placeController;

		@Inject
		public AboutActivity(IAboutView aboutView, PlaceController placeController) {
			this.placeController = placeController;
			this.aboutView = aboutView;
		}
		
		@Override
		public void start(AcceptsOneWidget panel, EventBus eventBus) {
			aboutView.setPresenter(this);
			panel.setWidget(aboutView.asWidget());
		}

		@Override
		public void update() {
			// TODO Auto-generated method stub
			
		}

	}



