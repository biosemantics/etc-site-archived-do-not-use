package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.MyActivityManager;
import com.google.gwt.activity.shared.MyActivityMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

public class ContentActivityManagerProvider implements Provider<MyActivityManager> {

	private EventBus eventBus;
	private MyActivityMapper activityMapper;

	@Inject
	public ContentActivityManagerProvider(@Named("Content")MyActivityMapper activityMapper,
			@Named("ActivitiesBus")EventBus eventBus) {
		this.eventBus = eventBus;
		this.activityMapper = activityMapper;
	}
	
	@Override
	public MyActivityManager get() {
		MyActivityManager activityManager = new MyActivityManager(activityMapper, eventBus);
		return activityManager;
	}

}
