package edu.arizona.biosemantics.etcsite.client.layout;

import com.google.gwt.activity.shared.MyActivity;
import com.google.gwt.activity.shared.MyActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.client.help.HelpActivity;

public class HelpActivityMapper implements MyActivityMapper {

	private HelpActivity helpActivity;

	@Inject
	public HelpActivityMapper(HelpActivity helpActivity) {
		this.helpActivity = helpActivity;
	}
	
	@Override
	public MyActivity getActivity(Place place) {
		return helpActivity;
	}
}
