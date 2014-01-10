package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

public class MenuActivityManagerProvider implements Provider<ActivityManager> {

	private EventBus eventBus;
	private ActivityMapper activityMapper;

	@Inject
	public MenuActivityManagerProvider(@Named("Menu")ActivityMapper activityMapper, 
			@Named("ActivitiesBus")EventBus eventBus) {
		this.eventBus = eventBus;
		this.activityMapper = activityMapper;
	}
	
	@Override
	public ActivityManager get() {
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		return activityManager;
	}
}
