package edu.arizona.biosemantics.etcsite.client.content.matrixGeneration.review;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.NativeEvent;

public class TaxonDnDHandler implements DnDHandler {

	private int draggedTaxon = -1;
	private ReviewView viewImpl;
	
	public TaxonDnDHandler(ReviewView viewImpl) {
		this.viewImpl = viewImpl;
	}
	
	@Override
	public void onDragStart(Context context, NativeEvent event) {
		draggedTaxon = context.getIndex();
	}
	
	@Override
	public void onDrop(Context context, NativeEvent event) {
		if(draggedTaxon != -1)
			viewImpl.moveTaxon(draggedTaxon, context.getIndex());
		draggedTaxon = -1;
	}
	
}
