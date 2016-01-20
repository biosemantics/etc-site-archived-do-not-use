package edu.arizona.biosemantics.etcsitehelpcenter.client;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpServiceAsync;
import edu.arizona.biosemantics.etcsitehelpcenter.client.layout.EtcSiteHelpPresenter;
import edu.arizona.biosemantics.etcsitehelpcenter.client.layout.EtcSiteHelpView;
import edu.arizona.biosemantics.etcsitehelpcenter.client.layout.IEtcSiteHelpView;

public class ClientModule extends AbstractGinModule {
	
	//convention: don't set view as singleton, unless for good reason. 
	//A view should in the general case be a widget, which can only be attached to one parent at a time.
	//If defined as singleton, it will with multiple use be attached to a new parent, hence disappear in another view.
	//This is usually not the desired behavior. 
	//Use providers or make presenter singleton and responsible of view/return the view when necessary for third party
	protected void configure() {
		//views, presenter
		bind(IEtcSiteHelpView.class).to(EtcSiteHelpView.class);
		bind(IEtcSiteHelpView.Presenter.class).to(EtcSiteHelpPresenter.class).in(Singleton.class);
		
		//activites, places, eventbus
		/*bind(EventBus.class).annotatedWith(Names.named("ActivitiesBus")).to(SimpleEventBus.class).in(Singleton.class);
		bind(PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);
		bind(MyActivityMapper.class).annotatedWith(Names.named("Content")).
			to(ContentActivityMapper.class).in(Singleton.class);
		bind(MyActivityManager.class).annotatedWith(Names.named("Content")).
			toProvider(ContentActivityManagerProvider.class).in(Singleton.class);
		bind(ActivityManager.class).annotatedWith(Names.named("Help")).
			toProvider(HelpActivityManagerProvider.class).in(Singleton.class);
		bind(ActivityMapper.class).annotatedWith(Names.named("Help")).
			to(HelpActivityMapper.class).in(Singleton.class);
		bind(Place.class).annotatedWith(Names.named("DefaultPlace")).to(HomePlace.class);
		
		bind(PlaceHistoryMapper.class).to(MyPlaceHistoryMapper.class).in(Singleton.class);
		bind(PlaceHistoryHandler.class).toProvider(PlaceHistoryHandlerProvider.class).in(Singleton.class);
		
		bind(EventBus.class).annotatedWith(Names.named("EtcSite")).to(SimpleEventBus.class).in(Singleton.class);
		bind(EventBus.class).annotatedWith(Names.named("AnnotationReview")).to(SimpleEventBus.class).in(Singleton.class);
		*/
		//services
		bind(IHelpServiceAsync.class).in(Singleton.class);
		
		//misc
		
	}
}