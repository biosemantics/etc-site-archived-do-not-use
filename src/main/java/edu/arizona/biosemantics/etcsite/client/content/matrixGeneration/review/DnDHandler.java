package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.NativeEvent;

public interface DnDHandler {

	
	public void onDragStart(Context context, NativeEvent event);
	
	public void onDrop(Context context, NativeEvent event);
}
