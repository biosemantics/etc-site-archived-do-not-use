package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.sirls.etc.site.shared.rpc.db.Task;
import edu.arizona.sirls.etc.site.shared.rpc.db.VisualizationConfiguration;

public class VisualizationEvent extends GwtEvent<VisualizationEventHandler> {

	public static Type<VisualizationEventHandler> TYPE = new Type<VisualizationEventHandler>();
	
	private VisualizationConfiguration visualizationConfiguration;
	
	public VisualizationEvent() { }
	
	public VisualizationEvent(VisualizationConfiguration visualizationConfiguration) {
		this.visualizationConfiguration = visualizationConfiguration;
	}


	public VisualizationConfiguration getVisualizationConfiguration() {
		return visualizationConfiguration;
	}

	public void setVisualizationConfiguration(VisualizationConfiguration visualizationConfiguration) {
		this.visualizationConfiguration = visualizationConfiguration;
	}

	@Override
	public Type<VisualizationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(VisualizationEventHandler handler) {
		handler.onVisualization(this);
	}
}
