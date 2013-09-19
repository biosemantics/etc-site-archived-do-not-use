package edu.arizona.sirls.etc.site.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface PipelineEventHandler extends EventHandler {

	void onPipeline(PipelineEvent pipelineEvent);

}
