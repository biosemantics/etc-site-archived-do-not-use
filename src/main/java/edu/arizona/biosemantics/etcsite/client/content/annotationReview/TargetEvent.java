package edu.arizona.biosemantics.etcsite.client.content.annotationReview;

import com.google.gwt.event.shared.GwtEvent;

public class TargetEvent extends GwtEvent<TargetEventHandler> {

	public static Type<TargetEventHandler> TYPE = new Type<TargetEventHandler>();
	private String target;
	
	public TargetEvent(String target) {
		this.target = target;
	}

	@Override
	public Type<TargetEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TargetEventHandler handler) {
		handler.onTarget(this);
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	

}
