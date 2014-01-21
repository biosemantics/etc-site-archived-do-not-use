package edu.arizona.biosemantics.otolite.client.event.to_ontologies;

import com.google.gwt.event.shared.EventHandler;

public interface OntologyRecordSelectChangedEventHandler extends EventHandler {
	void onSelect(OntologyRecordSelectChangedEvent event);
}
