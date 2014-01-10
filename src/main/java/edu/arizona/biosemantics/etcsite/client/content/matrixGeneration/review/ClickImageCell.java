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

public class ClickImageCell extends ImageCell {

	private Set<String> consumedEvents;
	private ClickHandler clickHandler;

	public ClickImageCell(ClickHandler clickHandler) {
		super();
		this.clickHandler = clickHandler;
		consumedEvents = new HashSet<String>();
		consumedEvents.add(BrowserEvents.CLICK);
		consumedEvents.add(BrowserEvents.DBLCLICK);
		consumedEvents.add(BrowserEvents.CONTEXTMENU);
		consumedEvents = Collections.unmodifiableSet(consumedEvents);
	}

	@Override
	public Set<String> getConsumedEvents() {
		return this.consumedEvents;
	}
	
	@Override
	public void	onBrowserEvent(Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		if(event.getType().equals(BrowserEvents.CLICK)) 
			clickHandler.onClick(context, event);
		if(event.getType().equals(BrowserEvents.DBLCLICK)) 
			clickHandler.onDoubleClick(context, event);
		if(event.getType().equals(BrowserEvents.CONTEXTMENU)) 
			clickHandler.onContext(context, event);
		
	}
	
}
