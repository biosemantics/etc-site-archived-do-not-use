package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.help.HelpHomeActivity;
import edu.arizona.biosemantics.etcsite.client.help.HelpHomePlace;
import edu.arizona.biosemantics.etcsite.client.help.HelpSemanticMarkupActivity;
import edu.arizona.biosemantics.etcsite.client.help.HelpSemanticMarkupPlace;

public class HelpActivityMapper implements ActivityMapper {

	private HelpSemanticMarkupActivity helpSemanticMarkupActivity;
	private HelpHomeActivity helpHomeActivity;
	private Activity currentActivity;

	@Inject
	public HelpActivityMapper(HelpSemanticMarkupActivity helpSemanticMarkupActivity, HelpHomeActivity helpHomeActivity) {
		this.helpSemanticMarkupActivity = helpSemanticMarkupActivity;
		this.helpHomeActivity = helpHomeActivity;
	}
	
	@Override
	public Activity getActivity(Place place) {
		if(currentActivity == null)
			currentActivity = helpHomeActivity;
		
		if(place instanceof HelpHomePlace) 
			currentActivity = helpHomeActivity;
		if(place instanceof HelpSemanticMarkupPlace)
			currentActivity = helpSemanticMarkupActivity;
		
		return currentActivity;
	}
}
