package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

public class DnDImageCell extends ImageCell {

	private Set<String> consumedEvents;
	private DnDHandler dndHandler;

	public DnDImageCell(DnDHandler dndHandler) {
		super();
		this.dndHandler = dndHandler;
		consumedEvents = new HashSet<String>();
		/*consumedEvents.add(BrowserEvents.CLICK);
		consumedEvents.add(BrowserEvents.DBLCLICK);
		consumedEvents.add(BrowserEvents.CONTEXTMENU);
		consumedEvents.add(BrowserEvents.DRAG);
		consumedEvents.add(BrowserEvents.DRAGEND);
		consumedEvents.add(BrowserEvents.DRAGENTER);
		consumedEvents.add(BrowserEvents.DRAGLEAVE);
		consumedEvents.add(BrowserEvents.DRAGOVER);*/
		
		consumedEvents.add(BrowserEvents.DRAGSTART);
		consumedEvents.add(BrowserEvents.DRAGENTER);
		consumedEvents.add(BrowserEvents.DRAGLEAVE);
		consumedEvents.add(BrowserEvents.DRAGOVER);
		consumedEvents.add(BrowserEvents.DROP);
		consumedEvents = Collections.unmodifiableSet(consumedEvents);
	}

	@Override
	public Set<String> getConsumedEvents() {
		return this.consumedEvents;
	}
	
	@Override
	public void	onBrowserEvent(Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		//System.out.println(event.getType());
		if(event.getType().equals(BrowserEvents.DRAGSTART))
			dndHandler.onDragStart(context, event);
		if(event.getType().equals(BrowserEvents.DROP))
			dndHandler.onDrop(context, event);
		
	}
	
}
